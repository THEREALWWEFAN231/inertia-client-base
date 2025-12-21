package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;

import java.awt.Color;

public class DistanceColorUtils {

    //generated with chat gbt :sunglasses:
    //Can you give me java code that smoothly transitions colors based on distance, red if distance is less then 10, orange if distance is  less then 20, green otherwise
    public static Color interpolateColor(float distance) {
        if (distance < 10) {
            return UIUtils.transitionColor(Color.RED, Color.ORANGE, distance / 10);
        } else if (distance < 20) {
            return UIUtils.transitionColor(Color.ORANGE, Color.GREEN, (distance - 10) / 10);
        } else {
            return Color.GREEN;
        }
    }

    public static Color interpolateColorSquaredDistance(float squaredDistance) {
        return interpolateColor(Mth.sqrt(squaredDistance));
    }

    public static Color getColorForEntity(Entity entity) {
        double distance = InertiaBase.mc.getCameraEntity().distanceTo(entity);
        return interpolateColor((float) distance);
    }

}
