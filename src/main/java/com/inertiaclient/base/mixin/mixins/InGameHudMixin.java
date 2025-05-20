package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl._2DEvent;
import com.inertiaclient.base.hud.HudEditorScreen;
import com.inertiaclient.base.render._2D3DRender;
import com.inertiaclient.base.render.skia.SkiaOpenGLInstance;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private static Identifier POWDER_SNOW_OUTLINE;

    @Shadow
    public abstract void clear();

    @Unique
    private SkiaOpenGLInstance inertiaClient$skiaInstance;

    @Inject(method = "render", at = @At("HEAD"))
    private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo callbackInfo) {
        _2D3DRender.render(tickCounter.getTickDelta(false), context.getMatrices(), true);
    }

    @Inject(method = "renderMainHud", at = @At("HEAD"))
    private void renderMainHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo callbackInfo) {
        context.draw();//draw hud, cross hair and crap before we do our crap, this might mess stuff up?

        EventManager.register(new _2DEvent(context, tickCounter.getTickDelta(false)));
        if (!(InertiaBase.mc.currentScreen instanceof HudEditorScreen)) {
            if (this.inertiaClient$skiaInstance == null) {
                this.inertiaClient$skiaInstance = new SkiaOpenGLInstance();
            }
            InertiaBase.instance.getHudManager().beforeRender(this.inertiaClient$skiaInstance, false);
            InertiaBase.instance.getHudManager().render(context, context.getScaledWindowWidth(), context.getScaledWindowHeight(), false);
        }
    }

}