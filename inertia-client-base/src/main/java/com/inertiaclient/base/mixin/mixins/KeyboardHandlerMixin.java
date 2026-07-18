package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.KeyActionEvent;
import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.module.Module;
import com.inertiaclient.base.utils.InputUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.sdl.SDLScancode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    //@Inject(method = "onKey", at = @At(value = "INVOKE", target = "net/minecraft/client/util/InputUtil.isKeyPressed(JI)Z", ordinal = 5), cancellable = true)
    @Inject(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getDebugOverlay()Lnet/minecraft/client/gui/components/DebugScreenOverlay;", ordinal = 0))
    private void onKeyPress(long handle, @KeyEvent.Action int action, KeyEvent event, CallbackInfo callbackInfo) {
        if (InertiaBase.mc.gui.screen() != null) {
            return;
        }
        InputConstants.Key input = InputUtils.fromKeyCode(event.shortcutKey());
        if (!InputUtils.isScancodePressed(SDLScancode.SDL_SCANCODE_F3)) {
            if (InertiaBase.instance.getSettings().getClickGuiSettings().getKeybind().getValue() == input) {
                InertiaBase.mc.gui.setScreen(ModernClickGui.MODERN_CLICK_GUI);
            }

            for (Module module : InertiaBase.instance.getModuleManager().getModules()) {
                if (module.getBind() == input) {
                    module.toggle();
                }
            }

            KeyActionEvent inertiaKeyEvent = new KeyActionEvent(event.shortcutKey(), event.key(), action == KeyActionEvent.MINECRAFT_ACTION_PRESS || action == KeyActionEvent.MINECRAFT_ACTION_REPEAT, action);//could use != GLFW_RELEASE release, but this is more clear
            EventManager.fire(inertiaKeyEvent);
            if (inertiaKeyEvent.isCancelled()) {
                callbackInfo.cancel();
            }
        }
    }

}
