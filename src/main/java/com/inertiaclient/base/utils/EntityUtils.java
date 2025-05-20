package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.value.impl.EntityTypeValue;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;

public class EntityUtils {

    public static float getDistanceToPositionNoY(double x, double z) {
        float xDifference = (float) (InertiaBase.mc.player.getX() - x);
        float zDifference = (float) (InertiaBase.mc.player.getZ() - z);
        return MathHelper.sqrt(xDifference * xDifference + zDifference * zDifference);
    }

    public static float getDistanceToEntityNoY(Entity entity) {
        return getDistanceToPositionNoY(entity.getX(), entity.getZ());
    }

    public static float getDistanceToBlockEntityNoY(BlockEntity blockEntity) {
        return getDistanceToPositionNoY(blockEntity.getPos().getX() + .5f, blockEntity.getPos().getZ() + .5f);
    }

    public static boolean isPlayer(Entity entity) {
        if (entity instanceof ClientPlayerEntity) {
            return true;
        }
        if (InertiaBase.mc.player.hasVehicle() && InertiaBase.mc.player.getVehicle() == entity) {
            return true;
        }
        return false;
    }

    public static boolean shouldPlayerTarget(Entity entity, float range, boolean invisibles, boolean walls) {
        if (entity == null) {
            return false;
        }
        if (InertiaBase.mc.player.distanceTo(entity) > range) {
            return false;
        }
        //looks like this isn't really "working" any more, looks like the client is forced to use getHealth, which is fine
        if (entity.isRemoved()) {
            return false;
        }
        if (entity instanceof LivingEntity livingEntity && livingEntity.getHealth() <= 0) {
            return false;
        }
        if (entity.equals(InertiaBase.mc.player.getVehicle())) {
            return false;
        }
        if (!invisibles && entity.isInvisible()) {
            return false;
        }
        if (!walls && !InertiaBase.mc.player.canSee(entity)) {
            return false;
        }

        return true;
    }

    public static boolean shouldPlayerTarget(Entity entity, float range, boolean invisibles, boolean walls, EntityTypeValue targets) {
        if (!targets.isTargeted(entity)) {
            return false;
        }
        return shouldPlayerTarget(entity, range, invisibles, walls);
    }

    public static Comparator<Entity> comparatorByHealth() {
        return (entity, t1) -> {
            if (entity instanceof LivingEntity && !(t1 instanceof LivingEntity)) {
                return -1;
            }
            if (t1 instanceof LivingEntity && !(entity instanceof LivingEntity)) {
                return 1;
            }
            if (!(t1 instanceof LivingEntity) && !(entity instanceof LivingEntity)) {
                return 0;
            }
            LivingEntity entity1 = (LivingEntity) entity;
            LivingEntity entity2 = (LivingEntity) t1;
            return Float.compare(entity1.getHealth() + entity1.getAbsorptionAmount(), entity2.getHealth() + entity2.getAbsorptionAmount());
        };
    }

    public static double getHealthAndAbsorption(LivingEntity entity) {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

}
