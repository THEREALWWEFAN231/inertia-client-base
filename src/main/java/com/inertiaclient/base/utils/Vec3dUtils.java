package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class Vec3dUtils {

    public static Vec3d setX(Vec3d vec3d, double newValue) {
        return new Vec3d(newValue, vec3d.y, vec3d.z);
    }

    public static Vec3d setY(Vec3d vec3d, double newValue) {
        return new Vec3d(vec3d.x, newValue, vec3d.z);
    }

    public static Vec3d setZ(Vec3d vec3d, double newValue) {
        return new Vec3d(vec3d.x, vec3d.y, newValue);
    }

    public static Vec3d setX(Vec3d vec3d, double newValue, Vec3dMath math) {
        return new Vec3d(math.performMath(vec3d.x, newValue), vec3d.y, vec3d.z);
    }

    public static Vec3d setY(Vec3d vec3d, double newValue, Vec3dMath math) {
        return new Vec3d(vec3d.x, math.performMath(vec3d.y, newValue), vec3d.z);
    }

    public static Vec3d setZ(Vec3d vec3d, double newValue, Vec3dMath math) {
        return new Vec3d(vec3d.x, vec3d.y, math.performMath(vec3d.z, newValue));
    }

    public static class Velocity {

        public static void setEntityX(Entity entity, double newValue, Vec3dMath math) {
            entity.setVelocity(Vec3dUtils.setX(entity.getVelocity(), newValue, math));
        }

        public static void setEntityY(Entity entity, double newValue, Vec3dMath math) {
            entity.setVelocity(Vec3dUtils.setY(entity.getVelocity(), newValue, math));
        }

        public static void setEntityZ(Entity entity, double newValue, Vec3dMath math) {
            entity.setVelocity(Vec3dUtils.setZ(entity.getVelocity(), newValue, math));
        }

        public static void setPlayerX(double newValue, Vec3dMath math) {
            Vec3dUtils.Velocity.setEntityX(InertiaBase.mc.player, newValue, math);
        }

        public static void setPlayerY(double newValue, Vec3dMath math) {
            Vec3dUtils.Velocity.setEntityY(InertiaBase.mc.player, newValue, math);
        }

        public static void setPlayerZ(double newValue, Vec3dMath math) {
            Vec3dUtils.Velocity.setEntityZ(InertiaBase.mc.player, newValue, math);
        }
    }

    public static class Position {

        public static void setEntityX(Entity entity, double newValue, Vec3dMath math) {
            entity.setPos(math.performMath(entity.getX(), newValue), entity.getY(), entity.getZ());
        }

        public static void setEntityY(Entity entity, double newValue, Vec3dMath math) {
            entity.setPos(entity.getX(), math.performMath(entity.getY(), newValue), entity.getZ());
        }

        public static void setEntityZ(Entity entity, double newValue, Vec3dMath math) {
            entity.setPos(entity.getX(), entity.getY(), math.performMath(entity.getZ(), newValue));
        }

        public static void setPlayerX(double newValue, Vec3dMath math) {
            Vec3dUtils.Position.setEntityX(InertiaBase.mc.player, newValue, math);
        }

        public static void setPlayerY(double newValue, Vec3dMath math) {
            Vec3dUtils.Position.setEntityY(InertiaBase.mc.player, newValue, math);
        }

        public static void setPlayerZ(double newValue, Vec3dMath math) {
            Vec3dUtils.Position.setEntityZ(InertiaBase.mc.player, newValue, math);
        }

    }

    public enum Vec3dMath {

        ADD, SUBTRACT, MULTIPLY, DIVIDE, SET;

        public double performMath(double old, double input) {
            if (this == ADD) {
                return old + input;
            } else if (this == SUBTRACT) {
                return old - input;
            } else if (this == MULTIPLY) {
                return old * input;
            } else if (this == DIVIDE) {
                return old / input;
            } else if (this == SET) {
                return input;
            }
            return 0;
        }

    }

}
