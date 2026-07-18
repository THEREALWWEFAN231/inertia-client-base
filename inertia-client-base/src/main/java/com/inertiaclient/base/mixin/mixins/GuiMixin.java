package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "extractRenderState", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;screen:Lnet/minecraft/client/gui/screens/Screen;", ordinal = 0))
    public void extractRenderState(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo callbackInfo, @Local GuiGraphicsExtractor graphics, @Local(ordinal = 0) int mouseX, @Local(ordinal = 1) int mouseY) {
        if (InertiaBase.mc.gui.screen() != ModernClickGui.MODERN_CLICK_GUI && AnimationValue.tweenEngine.containsTarget(ModernClickGui.MODERN_CLICK_GUI.getMainFrame().getAnimationValue())) {
            //ModernClickGui.MODERN_CLICK_GUI.render(drawContextLocalRef.get(), mouseXRef.get(), mouseYRef.get(), InertiaClient.mc.getLastFrameDuration());

            ModernClickGui.MODERN_CLICK_GUI.betterRender(graphics, mouseX, mouseY, deltaTracker.getGameTimeDeltaTicks());
            /*ModernClickGui.MODERN_CLICK_GUI.getSkiaInstance().setup(ModernClickGui.MODERN_CLICK_GUI.getCachedFrameBuffer(), () -> {
                ModernClickGui.MODERN_CLICK_GUI.getMainFrame().draw(drawContextLocal, mouseX, mouseY, 0, 0, tickCounter.getLastDuration(), ModernClickGui.MODERN_CLICK_GUI.getSkiaInstance().getCanvasWrapper());
            });*/
        }
    }

}
