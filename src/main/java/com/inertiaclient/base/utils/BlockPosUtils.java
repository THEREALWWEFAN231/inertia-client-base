package com.inertiaclient.base.utils;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;

import static com.inertiaclient.base.InertiaBase.mc;

public class BlockPosUtils {

    public static ArrayList<BlockPos> getAllInBoxPlayerDirectionClosest(int forwards, int backwards, int left, int right, int up, int down) {
        ArrayList<BlockPos> positions = getAllInBoxPlayerDirection(forwards, backwards, left, right, up, down);
        BlockPosUtils.sortByClosest(positions);

        return positions;
    }

    public static void sortByClosest(ArrayList<BlockPos> positions) {
        positions.sort(new Comparator<BlockPos>() {

            @Override
            public int compare(BlockPos first, BlockPos second) {
                //yes 0.5 is needed, to correctly 100% choose the closest block, we don't need to square root it...
                return Double.compare(mc.player.squaredDistanceTo(first.getX() + 0.5, first.getY() + 0.5, first.getZ() + 0.5), mc.player.squaredDistanceTo(second.getX() + 0.5, second.getY() + 0.5, second.getZ() + 0.5));
            }
        });
    }

    public static ArrayList<BlockPos> getAllInBoxPlayerDirection(int forwards, int backwards, int left, int right, int up, int down) {

        BlockPos from = BlockPos.ofFloored(mc.player.getX(), mc.player.getY() - down, mc.player.getZ()).offset(mc.player.getHorizontalFacing(), -backwards).offset(mc.player.getHorizontalFacing().rotateYClockwise(), right);
        BlockPos to = BlockPos.ofFloored(mc.player.getX(), mc.player.getY() + up, mc.player.getZ()).offset(mc.player.getHorizontalFacing(), forwards).offset(mc.player.getHorizontalFacing().rotateYCounterclockwise(), left);
        from = from.offset(mc.player.getHorizontalFacing(), 0);
        to = to.offset(mc.player.getHorizontalFacing(), 0);

        return getAllInBox(from, to);
    }

    public static ArrayList<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
        return BlockPosUtils.getAllInBox(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()), Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
    }

    public static ArrayList<BlockPos> getAllInBox(int startX, int startY, int startZ, int endX, int endY, int endZ) {

        if (startX == endX || startY == endY || startZ == endZ) {
            //return new ArrayList<BlockPos>();
        }

        ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
        for (int x = startX; x < endX + 1; x++) {
            for (int z = startZ; z < endZ + 1; z++) {
                for (int y = startY; y < endY + 1; y++) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

}
