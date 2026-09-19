package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl._2DEvent;
import com.inertiaclient.base.hud.HudEditorScreen;
import com.inertiaclient.base.render.skia.instances.SkiaInstance;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class HudMixin {

    @Unique
    private SkiaInstance inertiaClient$skiaInstance;

    @Inject(method = "extractHotbarAndDecorations", at = @At("HEAD"))
    private void renderMainHud(final GuiGraphicsExtractor graphics, final DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
        EventManager.fire(new _2DEvent(graphics, deltaTracker));
        if (!(InertiaBase.mc.gui.screen() instanceof HudEditorScreen)) {
            if (this.inertiaClient$skiaInstance == null) {
                this.inertiaClient$skiaInstance = SkiaInstance.create((graphics1, mouseX, mouseY, delta) -> {
                    InertiaBase.instance.getHudManager().beforeRender(this.inertiaClient$skiaInstance, false);
                    InertiaBase.instance.getHudManager().renderGroups(graphics1, graphics.guiWidth(), graphics.guiHeight(), false);
                });
            }

            this.inertiaClient$skiaInstance.drawAndRender(graphics, -999, -999, deltaTracker.getGameTimeDeltaPartialTick(false));
        }
    }

}