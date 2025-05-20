package com.inertiaclient.base.utils;

import net.minecraft.item.AirBlockItem;
import net.minecraft.item.ItemStack;

public class ItemUtils {

    public static boolean isAir(ItemStack itemStack) {
        return itemStack == null || itemStack.getItem() instanceof AirBlockItem;
    }

}
