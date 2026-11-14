package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl.BlockUpdateEvent;
import com.inertiaclient.base.event.impl.ChunkLoadEvent;
import com.inertiaclient.base.event.impl.ChunkUnloadEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import static com.inertiaclient.base.InertiaBase.mc;

//AI made/refactored most of this
public class BlockSearcher {

    private final HashMap<Object, HashSet<Target>> subscribers = new HashMap<>();
    //the exact block states that are being searched for, mapped to the target that is searching for them.
    //concurrent so the scan thread can read them without a snapshot, they are only mutated on the game thread
    private final ConcurrentHashMap<BlockState, Target> exactStateTargets = new ConcurrentHashMap<>();
    //the blocks that are being searched for, mapped to the target that is searching for them
    private final ConcurrentHashMap<Block, Target> blockTargets = new ConcurrentHashMap<>();

    private final HashMap<BlockPos, Target> positionsWithTargets = new HashMap<>();
    private final HashMap<Target, Set<BlockPos>> targetsByPositions = new HashMap<>();
    private final HashMap<ChunkPos, Set<BlockPos>> foundPositionsByChunk = new HashMap<>();

    //checks if a block state matches any current target
    private final Predicate<BlockState> targetFilter = state -> this.exactStateTargets.containsKey(state) || this.blockTargets.containsKey(state.getBlock());

    private final ExecutorService scanExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "BlockSearcher-Scan");
        thread.setDaemon(true);
        return thread;
    });
    //if this is set again while a scan is running, the running scan aborts and starts over from the first chunk
    private volatile boolean fullProcessQueued;

    @EventTarget
    private final EventListener<ChunkLoadEvent> chunkLoadListener = this::onChunkLoad;
    @EventTarget
    private final EventListener<ChunkUnloadEvent> chunkUnloadListener = this::onChunkUnload;
    @EventTarget
    private final EventListener<BlockUpdateEvent> blockUpdateListener = this::onBlockUpdate;

    private volatile ClientLevel lastWorld;

    public BlockSearcher() {
        EventManager.register(this);
    }

    public synchronized void addTarget(Object subscriber, Target target) {
        if (target == null) {
            return;
        }

        boolean isNewToSubscriber = this.subscribers.computeIfAbsent(subscriber, o -> new HashSet<>()).add(target);
        if (isNewToSubscriber && this.addSearchingTarget(target)) {
            this.scheduleFullProcess();
        }
    }

    public synchronized void addTargets(Object subscriber, Collection<Target> targets) {
        if (targets == null) {
            return;
        }

        boolean shouldScheduleProcess = false;
        var subscriberTargets = this.subscribers.computeIfAbsent(subscriber, o -> new HashSet<>());
        for (Target target : targets) {
            if (target == null) {
                continue;
            }

            boolean isNewToSubscriber = subscriberTargets.add(target);
            if (isNewToSubscriber && this.addSearchingTarget(target)) {
                shouldScheduleProcess = true;
            }
        }
        if (shouldScheduleProcess) {
            this.scheduleFullProcess();
        }

    }

    /**
     * Replaces all targets a subscriber is looking for. Found positions of removed targets are dropped immediately,
     * a full rescan is only queued if a target was added that wasn't already being searched for.
     */
    public synchronized void setSubscriberTargets(Object subscriber, Collection<Target> targets) {
        System.out.println("set");
        HashSet<Target> subscriberTargets = this.subscribers.computeIfAbsent(subscriber, o -> new HashSet<>());

        //forget targets the subscriber no longer wants, removing a target never needs a rescan,
        //its found positions are dropped right here
        for (Target target : subscriberTargets.toArray(new Target[0])) {
            if (targets == null || !targets.contains(target)) {
                subscriberTargets.remove(target);
                this.removeTargetIfNoSubscribers(target);
            }
        }

        //add the new targets
        boolean shouldScheduleProcess = false;
        if (targets != null) {
            for (Target target : targets) {
                if (target == null) {
                    continue;
                }

                boolean isNewToSubscriber = subscriberTargets.add(target);
                if (isNewToSubscriber && this.addSearchingTarget(target)) {
                    shouldScheduleProcess = true;
                }
            }
        }

        if (shouldScheduleProcess) {
            this.scheduleFullProcess();
        }
    }

    public synchronized void removeSubscriber(Object subscriber) {
        HashSet<Target> targetsBySubscriber = this.subscribers.remove(subscriber);
        if (targetsBySubscriber == null) {
            return;
        }

        for (Target target : targetsBySubscriber) {
            this.removeTargetIfNoSubscribers(target);
        }
    }

    public synchronized void removeTarget(Object subscriber, Target target) {
        HashSet<Target> targetsBySubscriber = this.subscribers.get(subscriber);
        if (targetsBySubscriber != null && targetsBySubscriber.remove(target)) {
            this.removeTargetIfNoSubscribers(target);
        }
    }

    private synchronized void removeTargetIfNoSubscribers(Target target) {
        if (this.anySubscribersLookingFor(target)) {
            return;
        }

        //no subscriber is looking for this target anymore, remove it from the search and forget all of its found positions
        if (this.removeSearchingTarget(target)) {
            Set<BlockPos> positions = this.targetsByPositions.remove(target);
            if (positions != null) {
                for (BlockPos pos : positions) {
                    this.positionsWithTargets.remove(pos);
                    this.removeFromChunkMap(pos);
                }
            }
        }
    }

    public synchronized Set<BlockPos> getPositionsWithTarget(Target target) {
        Set<BlockPos> positions = this.targetsByPositions.get(target);
        if (positions == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet<>(positions));
    }

    public synchronized Target getTargetWithPosition(BlockPos pos) {
        return this.positionsWithTargets.get(pos);
    }

    private boolean anySubscribersLookingFor(Target target) {
        for (HashSet<Target> targets : this.subscribers.values()) {
            if (targets.contains(target)) {
                return true;
            }
        }
        return false;
    }

    private boolean addSearchingTarget(Target target) {
        if (target.blockState() != null) {
            return this.exactStateTargets.putIfAbsent(target.blockState(), target) == null;
        }
        return this.blockTargets.putIfAbsent(target.block(), target) == null;
    }

    private boolean removeSearchingTarget(Target target) {
        if (target.blockState() != null) {
            return this.exactStateTargets.remove(target.blockState()) != null;
        }
        return this.blockTargets.remove(target.block()) != null;
    }

    private boolean isSearching() {
        return !this.exactStateTargets.isEmpty() || !this.blockTargets.isEmpty();
    }


    private synchronized void scheduleFullProcess() {
        if (!this.isSearching() || this.fullProcessQueued) {
            return;
        }

        this.fullProcessQueued = true;
        this.scanExecutor.execute(this::runScanLoop);
    }

    private void runScanLoop() {
        while (true) {
            boolean shouldScan;
            synchronized (this) {
                shouldScan = this.fullProcessQueued;
                this.fullProcessQueued = false;
            }

            if (!shouldScan) {
                return;
            }

            try {
                this.processAllLoadedChunksOnScanThread();
            } catch (Throwable t) {
                InertiaBase.LOGGER.error("BlockSearcher failed to process all loaded chunks", t);
            }
        }
    }

    private void processAllLoadedChunksOnScanThread() {
        ClientLevel world = mc.level;
        if (world == null || mc.player == null || !this.isSearching()) {
            return;
        }

        int viewDistance = mc.options.renderDistance().get();
        int playerChunkX = SectionPos.blockToSectionCoord(mc.player.getBlockX());
        int playerChunkZ = SectionPos.blockToSectionCoord(mc.player.getBlockZ());

        for (int x = -viewDistance; x <= viewDistance; x++) {
            for (int z = -viewDistance; z <= viewDistance; z++) {
                //another rescan was requested while this pass was running, or the world changed
                if (this.fullProcessQueued || this.lastWorld != world) {
                    return;
                }

                try {
                    LevelChunk chunk = world.getChunkSource().getChunkNow(playerChunkX + x, playerChunkZ + z);
                    if (chunk == null) {
                        continue;
                    }

                    //reads chunk data without holding the lock, the game thread is not blocked while scanning
                    HashMap<Target, HashSet<BlockPos>> found = new HashMap<>();
                    this.scanChunkInto(world, chunk, found);

                    synchronized (this) {
                        if (this.lastWorld == world) {
                            this.reconcileChunk(world, chunk.getPos(), found);
                        }
                    }
                } catch (Throwable t) {
                    //reading chunk data off the game thread can race with vanilla writes, never let one bad chunk kill the scan
                    InertiaBase.LOGGER.error("BlockSearcher failed to process chunk {}, {}", playerChunkX + x, playerChunkZ + z, t);
                }
            }
        }

        //forget found positions in chunks that are outside the render distance and no longer loaded:
        //the client can drop chunks silently when the view center moves, without firing ChunkUnloadEvent
        synchronized (this) {
            if (this.lastWorld == world) {
                for (ChunkPos foundChunk : this.foundPositionsByChunk.keySet().toArray(new ChunkPos[0])) {
                    if (Math.abs(foundChunk.x() - playerChunkX) > viewDistance || Math.abs(foundChunk.z() - playerChunkZ) > viewDistance) {
                        if (world.getChunkSource().getChunkNow(foundChunk.x(), foundChunk.z()) == null) {
                            this.removeFoundInChunk(foundChunk);
                        }
                    }
                }
            }
        }
    }

    /**
     * Merges scan results of a single chunk into the found positions. Every affected position is verified against the
     * live world state and the live searched targets, so changes that happened since the chunk was scanned are not clobbered.
     * Must be called synchronized.
     */
    private void reconcileChunk(ClientLevel world, ChunkPos chunkPos, HashMap<Target, HashSet<BlockPos>> found) {
        HashSet<BlockPos> scannedPositions = new HashSet<>();
        for (HashSet<BlockPos> positions : found.values()) {
            scannedPositions.addAll(positions);
        }

        //previously found positions the scan no longer sees: verify them against the live world before forgetting them
        Set<BlockPos> previousPositions = this.foundPositionsByChunk.get(chunkPos);
        if (previousPositions != null) {
            for (BlockPos pos : previousPositions.toArray(new BlockPos[0])) {
                if (scannedPositions.contains(pos)) {
                    continue;
                }

                Target currentTarget = this.getTargetForBlockState(world.getBlockState(pos));
                Target recordedTarget = this.positionsWithTargets.get(pos);
                if (currentTarget == null) {//the block is gone or no longer matches anything
                    this.removeFound(pos);
                } else if (!currentTarget.equals(recordedTarget)) {//the position is now matched by a different target
                    this.removeFound(pos);
                    this.addFound(chunkPos, pos, currentTarget);
                }
            }
        }

        //newly found positions, skipping ones that are already recorded correctly (e.g. by a block update while scanning)
        for (var entry : found.entrySet()) {
            for (BlockPos pos : entry.getValue()) {
                if (entry.getKey().equals(this.positionsWithTargets.get(pos))) {
                    continue;
                }

                Target currentTarget = this.getTargetForBlockState(world.getBlockState(pos));
                if (currentTarget == null) {//stale scan result, a block update already handled it
                    continue;
                }
                if (this.positionsWithTargets.containsKey(pos)) {
                    this.removeFound(pos);
                }
                this.addFound(chunkPos, pos, currentTarget);
            }
        }
    }

    /**
     * Scans a chunk and collects every position that matches a current target into the found map.
     * Only reads chunk data, doesn't touch any BlockSearcher state.
     */
    private void scanChunkInto(ClientLevel level, LevelChunk chunk, HashMap<Target, HashSet<BlockPos>> found) {
        ChunkPos chunkPos = chunk.getPos();
        LevelChunkSection[] sections = chunk.getSections();

        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            LevelChunkSection section = sections[sectionIndex];
            //skip sections that don't contain a single state any target could match
            if (section.hasOnlyAir() || !section.maybeHas(this.targetFilter)) {
                continue;
            }

            int baseY = level.getSectionYFromSectionIndex(sectionIndex) * 16;
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        Target target = this.getTargetForBlockState(section.getBlockState(x, y, z));
                        if (target != null) {
                            found.computeIfAbsent(target, t -> new HashSet<>()).add(new BlockPos(chunkPos.getMinBlockX() + x, baseY + y, chunkPos.getMinBlockZ() + z));
                        }
                    }
                }
            }
        }
    }

    /**
     * Scans a chunk for every block that is currently in the search, replacing any previously found positions in that chunk.
     * Runs on the game thread.
     */
    private synchronized void processChunk(ClientLevel level, LevelChunk chunk) {
        //remove if its already been processed
        this.removeFoundInChunk(chunk.getPos());

        if (!this.isSearching()) {
            return;
        }

        HashMap<Target, HashSet<BlockPos>> found = new HashMap<>();
        this.scanChunkInto(level, chunk, found);

        ChunkPos chunkPos = chunk.getPos();
        for (var entry : found.entrySet()) {
            for (BlockPos pos : entry.getValue()) {
                this.addFound(chunkPos, pos, entry.getKey());
            }
        }
    }

    /**
     * @return the search target that the given state matches, or null if it doesn't match any.
     * Safe to call from the scan thread, the target maps are only mutated on the game thread
     */
    private Target getTargetForBlockState(BlockState state) {
        if (state == null) {
            return null;
        }

        Target target = this.exactStateTargets.get(state);
        if (target != null) {
            return target;
        }
        return this.blockTargets.get(state.getBlock());
    }

    private synchronized void addFound(ChunkPos chunkPos, BlockPos pos, Target target) {
        this.positionsWithTargets.put(pos, target);
        this.targetsByPositions.computeIfAbsent(target, t -> new HashSet<>()).add(pos);
        this.foundPositionsByChunk.computeIfAbsent(chunkPos, t -> new HashSet<>()).add(pos);
    }

    private synchronized void removeFound(BlockPos pos) {
        Target target = this.positionsWithTargets.remove(pos);
        if (target == null) {
            return;
        }

        Set<BlockPos> positions = this.targetsByPositions.get(target);
        if (positions != null) {
            positions.remove(pos);
            if (positions.isEmpty()) {
                this.targetsByPositions.remove(target);
            }
        }

        this.removeFromChunkMap(pos);
    }

    private void removeFromChunkMap(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        Set<BlockPos> positions = this.foundPositionsByChunk.get(chunkPos);
        if (positions != null) {
            positions.remove(pos);
            if (positions.isEmpty()) {
                this.foundPositionsByChunk.remove(chunkPos);
            }
        }
    }

    /**
     * Removes all found positions inside a chunk.
     */
    private synchronized void removeFoundInChunk(ChunkPos chunkPos) {
        Set<BlockPos> positions = this.foundPositionsByChunk.remove(chunkPos);
        if (positions == null) {
            return;
        }

        for (BlockPos pos : positions) {
            //remove the found position
            Target target = this.positionsWithTargets.remove(pos);
            if (target != null) {
                Set<BlockPos> targetPositions = this.targetsByPositions.get(target);
                if (targetPositions != null) {
                    //remove the exact position from found blocks/targets
                    targetPositions.remove(pos);
                    if (targetPositions.isEmpty()) {//if we just removed the last found block/target of that type, completely remove the block/target as found
                        this.targetsByPositions.remove(target);
                    }
                }
            }
        }
    }

    private synchronized void clearFoundPositions() {
        this.positionsWithTargets.clear();
        this.targetsByPositions.clear();
        this.foundPositionsByChunk.clear();
    }

    private synchronized void onChunkLoad(ChunkLoadEvent event) {
        //if the world changed, everything we tracked before is invalid
        if (event.getWorld() != this.lastWorld) {
            this.lastWorld = event.getWorld();
            this.clearFoundPositions();
        }

        if (this.isSearching()) {
            this.processChunk(event.getWorld(), event.getChunk());
        }
    }

    private synchronized void onChunkUnload(ChunkUnloadEvent event) {
        this.removeFoundInChunk(event.getPos());
    }

    private synchronized void onBlockUpdate(BlockUpdateEvent event) {
        if (!this.isSearching()) {
            return;
        }

        BlockPos pos = event.getPos();
        Target foundTarget = this.positionsWithTargets.get(pos);

        Target target = this.getTargetForBlockState(event.getNewState());
        if (target == null) {
            //a searched block was replaced/removed, forget the old position
            if (foundTarget != null) {
                this.removeFound(pos);
            }
            return;
        }

        if (foundTarget == target) {//already recorded
            return;
        }

        //the position is now matched by a different target, replace the old record
        if (foundTarget != null) {
            this.removeFound(pos);
        }
        this.addFound(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4), pos, target);
    }

    //only one should be initiated
    public record Target(Block block, BlockState blockState) {

        public boolean matchesBlockState(BlockState matches) {
            if (matches == null) {
                return false;
            }

            if (this.block != null) {
                return this.block == matches.getBlock();
            }

            return this.blockState.equals(matches);
        }

        public static Target withBlock(Block block) {
            return new Target(block, null);
        }

        public static Target withBlockState(BlockState state) {
            return new Target(null, state);
        }

    }
}
