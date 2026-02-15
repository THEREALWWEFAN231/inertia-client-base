package com.inertiaclient.base.utils;

import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.ItemStack;

public class ItemUtils {

    public static boolean isAir(ItemStack itemStack) {
        return itemStack == null || itemStack.getItem() instanceof AirItem;
    }

}
