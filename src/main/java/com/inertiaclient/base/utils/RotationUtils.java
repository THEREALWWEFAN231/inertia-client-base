package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;


public class RotationUtils {

    //used to change the players head rotation
    @Getter
    private static float playerYaw;
    @Getter
    private static float playerPitch;
    @Getter
    private static TimerUtil rotationTimer = new TimerUtil();

    public static float[] getYawAndPitchToFaceXYZ(Entity from, double x, double y, double z, boolean doClientHeadRotation) {
        //math taken from EntityLiving, faceEntity
        double xDifference = x - from.getX();
        double yDifference = y - (from.getY() + (double) from.getEyeHeight());
        double zDifference = z - from.getZ();

        double distance = (double) Math.sqrt(xDifference * xDifference + zDifference * zDifference);
        float yaw = (float) (Mth.atan2(zDifference, xDifference) * (180D / Math.PI)) - 90.0F;
        float pitch = (float) (-(Mth.atan2(yDifference, distance) * (180D / Math.PI)));

        //fixes weird "snapping", basically updateRotation in EntityLiving
        yaw = from.getYRot() + Mth.wrapDegrees(yaw - from.getYRot());
        pitch = from.getXRot() + Mth.wrapDegrees(pitch - from.getXRot());
        if (doClientHeadRotation) {
            setPlayerHeadRotation(yaw, pitch);
        }

        return new float[]{yaw, pitch};
    }

    public static double getPreciseYawToXZ(double fromX, double fromZ, double x, double z) {
        //math taken from EntityLiving, faceEntity
        double xDifference = x - fromX;
        double zDifference = z - fromZ;

        double yaw = (Math.atan2(zDifference, xDifference) * (180D / Math.PI)) - 90;
        yaw = Mth.wrapDegrees(yaw);

        return yaw;
    }

    public static float[] getYawAndPitchToFaceXYZ(Entity from, double x, double y, double z) {
        return getYawAndPitchToFaceXYZ(from, x, y, z, true);
    }

    public static float[] getYawAndPitchToFaceEntityAddToY(Entity entity, double addToY) {
        double y = 0;

        if (entity instanceof LivingEntity) {
            LivingEntity entityLivingBase = (LivingEntity) entity;
            y = entityLivingBase.getY() + (double) entityLivingBase.getEyeHeight();
        } else {
            y = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D;
        }

        y += addToY;

        return getYawAndPitchToFaceXYZ(InertiaBase.mc.player, entity.getX(), y, entity.getZ());
    }

    public static float[] getYawAndPitchToFaceEntity(Entity entity) {
        return getYawAndPitchToFaceEntityAddToY(entity, 0);
    }

    public static float[] getYawAndPitchToBlockPos(BlockPos blockPos) {
        return getYawAndPitchToFaceXYZ(InertiaBase.mc.player, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }

    public static void setPlayerHeadRotation(float yaw, float pitch) {
        playerYaw = yaw;
        playerPitch = pitch;
        rotationTimer.reset();
    }

    //TODO: implment something better
    public static Direction getFacingFromBlock(BlockPos blockPos) {
        return Direction.UP;
    }
}
