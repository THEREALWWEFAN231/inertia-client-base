package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;

public class PlayerInteractionHelper {

    public static void interactItem(Player player, InteractionHand hand) {
        InteractionResult result = InertiaBase.mc.gameMode.useItem(player, hand);
        if (result instanceof InteractionResult.Success success) {
            if (success.swingSource() == InteractionResult.SwingSource.CLIENT) {
                InertiaBase.mc.player.swing(hand);
            }
        }
    }

}
