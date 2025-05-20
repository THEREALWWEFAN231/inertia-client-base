package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;

public class MovementUtils {

    public static double[] getMovement(double forwardMovement, double strafeMovement, float yaw, double speed) {
        if (forwardMovement == 0 && strafeMovement == 0) {
            return new double[2];//return two zeros
        } else {
            if (forwardMovement != 0) {
                if (strafeMovement > 0) {
                    yaw += forwardMovement > 0 ? -45 : 45;
                } else if (strafeMovement < 0) {
                    yaw += forwardMovement > 0 ? 45 : -45;
                }
                strafeMovement = 0;
                if (forwardMovement > 0) {
                    forwardMovement = 1;
                } else if (forwardMovement < 0) {
                    forwardMovement = -1;
                }
            }

            double cos = Math.cos(Math.toRadians(yaw + 90));
            double sin = Math.sin(Math.toRadians(yaw + 90));
            double x = forwardMovement * speed * cos + strafeMovement * speed * sin;
            double z = forwardMovement * speed * sin - strafeMovement * speed * cos;
            return new double[]{x, z};
        }
    }

    public static boolean isMoving(double forwardMovement, double strafeMovement) {
        return forwardMovement != 0 || strafeMovement != 0;
    }

    public static boolean isPlayerMoving() {
        return MovementUtils.isMoving(InertiaBase.mc.player.input.movementForward, InertiaBase.mc.player.input.movementSideways);
    }

    public static double[] getMovementForPlayer(double speed) {
        return MovementUtils.getMovement(InertiaBase.mc.player.input.movementForward, InertiaBase.mc.player.input.movementSideways, InertiaBase.mc.player.getYaw(), speed);
    }

}
