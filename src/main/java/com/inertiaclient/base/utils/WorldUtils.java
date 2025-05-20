package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;

import static com.inertiaclient.base.InertiaBase.mc;

public class WorldUtils {

    public static boolean isWater(Block block) {
        return block.getDefaultState().getFluidState().isIn(FluidTags.WATER);
    }

    public static boolean isWater(BlockState blockState) {
        return blockState.getFluidState().isIn(FluidTags.WATER);
    }

    public static boolean isWater(BlockPos blockPos) {
        return mc.world.getFluidState(blockPos).isIn(FluidTags.WATER);
    }

    public static boolean isLava(Block block) {
        return block.getDefaultState().getFluidState().isIn(FluidTags.LAVA);
    }

    public static boolean isLava(BlockState blockState) {
        return blockState.getFluidState().isIn(FluidTags.LAVA);
    }


    public static boolean isLava(BlockPos blockPos) {
        return mc.world.getFluidState(blockPos).isIn(FluidTags.LAVA);
    }

    public static boolean isLiquid(Block block) {
        return isWater(block) || isLava(block);
    }

    public static boolean isLiquid(BlockState blockState) {
        return isWater(blockState) || isLava(blockState);
    }

    public static boolean isLiquid(BlockPos blockPos) {
        return isWater(blockPos) || isLava(blockPos);
    }

    //i think this was taken from wurst a few years ago
    public static ArrayList<BlockEntity> getAllBlockEntities() {
        ArrayList<BlockEntity> blockEntities = new ArrayList<>();

        int viewDistance = InertiaBase.mc.options.getViewDistance().getValue();
        int playerChunkX = ChunkSectionPos.getSectionCoord(InertiaBase.mc.player.getBlockX());
        int playerChunkZ = ChunkSectionPos.getSectionCoord(InertiaBase.mc.player.getBlockZ());

        for (int x = -viewDistance; x <= viewDistance; x++) {
            for (int z = -viewDistance; z <= viewDistance; z++) {
                WorldChunk chunk = InertiaBase.mc.world.getChunkManager().getWorldChunk(playerChunkX + x, playerChunkZ + z);

                if (chunk != null) {
                    blockEntities.addAll(chunk.getBlockEntities().values());
                }
            }
        }

        return blockEntities;
    }

}
