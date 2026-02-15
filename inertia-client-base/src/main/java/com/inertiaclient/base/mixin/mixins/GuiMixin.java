package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl._2DEvent;
import com.inertiaclient.base.hud.HudEditorScreen;
import com.inertiaclient.base.render._2D3DRender;
import com.inertiaclient.base.render.skia.SkiaOpenGLInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    @Final
    private static ResourceLocation POWDER_SNOW_OUTLINE_LOCATION;

    @Shadow
    public abstract void onDisconnected();

    @Unique
    private SkiaOpenGLInstance inertiaClient$skiaInstance;

    @Inject(method = "render", at = @At("HEAD"))
    private void render(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo callbackInfo) {
        _2D3DRender.render(tickCounter.getGameTimeDeltaPartialTick(false), context.pose(), true);
    }

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
    private void renderMainHud(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo callbackInfo) {
        context.flush();//draw hud, cross hair and crap before we do our crap, this might mess stuff up?

        EventManager.register(new _2DEvent(context, tickCounter.getGameTimeDeltaPartialTick(false)));
        if (!(InertiaBase.mc.screen instanceof HudEditorScreen)) {
            if (this.inertiaClient$skiaInstance == null) {
                this.inertiaClient$skiaInstance = new SkiaOpenGLInstance();
            }
            InertiaBase.instance.getHudManager().beforeRender(this.inertiaClient$skiaInstance, false);
            InertiaBase.instance.getHudManager().render(context, context.guiWidth(), context.guiHeight(), false);
        }
    }

}