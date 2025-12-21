package com.inertiaclient.base.mixin.mixins;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    private boolean isInertiaUser;

}
