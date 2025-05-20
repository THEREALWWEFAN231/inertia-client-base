package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class PlayerInteractionHelper {

    public static void interactItem(PlayerEntity player, Hand hand) {
        ActionResult result = InertiaBase.mc.interactionManager.interactItem(player, hand);
        if (result instanceof ActionResult.Success success) {
            if (success.swingSource() == ActionResult.SwingSource.CLIENT) {
                InertiaBase.mc.player.swingHand(hand);
            }
        }
    }

}
