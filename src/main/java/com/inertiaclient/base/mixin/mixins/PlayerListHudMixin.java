package com.inertiaclient.base.mixin.mixins;

import net.minecraft.client.gui.hud.PlayerListHud;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    private boolean isInertiaUser;

}
