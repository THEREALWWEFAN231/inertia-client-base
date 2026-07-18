package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl._2DEvent;
import com.inertiaclient.base.hud.HudEditorScreen;
import com.inertiaclient.base.render.skia.SkiaVulkanInstance;
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
    private SkiaVulkanInstance inertiaClient$skiaInstance;

    //TODO: fix me
    /*@Inject(method = "render", at = @At("HEAD"))
    private void render(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo callbackInfo) {
        _2D3DRender.render(tickCounter.getGameTimeDeltaPartialTick(false), context.pose(), true);
    }*/

    @Inject(method = "extractHotbarAndDecorations", at = @At("HEAD"))
    private void renderMainHud(final GuiGraphicsExtractor graphics, final DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
        EventManager.register(new _2DEvent(graphics, deltaTracker));
        if (!(InertiaBase.mc.gui.screen() instanceof HudEditorScreen)) {
            if (this.inertiaClient$skiaInstance == null) {
                this.inertiaClient$skiaInstance = new SkiaVulkanInstance();
            }
            InertiaBase.instance.getHudManager().beforeRender(this.inertiaClient$skiaInstance, false);
            InertiaBase.instance.getHudManager().render(graphics, graphics.guiWidth(), graphics.guiHeight(), false);
        }
    }

}