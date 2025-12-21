package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;

import static com.inertiaclient.base.InertiaBase.mc;

public class WorldUtils {

    public static boolean isWater(Block block) {
        return block.defaultBlockState().getFluidState().is(FluidTags.WATER);
    }

    public static boolean isWater(BlockState blockState) {
        return blockState.getFluidState().is(FluidTags.WATER);
    }

    public static boolean isWater(BlockPos blockPos) {
        return mc.level.getFluidState(blockPos).is(FluidTags.WATER);
    }

    public static boolean isLava(Block block) {
        return block.defaultBlockState().getFluidState().is(FluidTags.LAVA);
    }

    public static boolean isLava(BlockState blockState) {
        return blockState.getFluidState().is(FluidTags.LAVA);
    }


    public static boolean isLava(BlockPos blockPos) {
        return mc.level.getFluidState(blockPos).is(FluidTags.LAVA);
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

        int viewDistance = InertiaBase.mc.options.renderDistance().get();
        int playerChunkX = SectionPos.blockToSectionCoord(InertiaBase.mc.player.getBlockX());
        int playerChunkZ = SectionPos.blockToSectionCoord(InertiaBase.mc.player.getBlockZ());

        for (int x = -viewDistance; x <= viewDistance; x++) {
            for (int z = -viewDistance; z <= viewDistance; z++) {
                LevelChunk chunk = InertiaBase.mc.level.getChunkSource().getChunkNow(playerChunkX + x, playerChunkZ + z);

                if (chunk != null) {
                    blockEntities.addAll(chunk.getBlockEntities().values());
                }
            }
        }

        return blockEntities;
    }

}
