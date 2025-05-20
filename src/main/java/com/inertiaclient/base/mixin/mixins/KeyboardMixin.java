package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.KeyActionEvent;
import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.module.Module;
import com.inertiaclient.base.utils.InputUtils;
import net.minecraft.client.Keyboard;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    //@Inject(method = "onKey", at = @At(value = "INVOKE", target = "net/minecraft/client/util/InputUtil.isKeyPressed(JI)Z", ordinal = 5), cancellable = true)
    @Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;switchF3State:Z", ordinal = 3))
    private void onKeyEvent(long window, int key, int scancode, int action, int modifiers, CallbackInfo callbackInfo) {
        InputUtil.Key input = InputUtils.fromKeyCode(key, scancode);
        if (!InputUtil.isKeyPressed(InertiaBase.mc.getWindow().getHandle(), GLFW.GLFW_KEY_F3)) {

            if (InertiaBase.instance.getSettings().getClickGuiSettings().getKeybind().getValue() == input) {
                InertiaBase.mc.setScreen(ModernClickGui.MODERN_CLICK_GUI);
            }

            for (Module module : InertiaBase.instance.getModuleManager().getModules()) {
                if (module.getBind() == input) {
                    module.toggle();
                }
            }

            KeyActionEvent event = new KeyActionEvent(key, scancode, action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT, action);//could use != GLFW_RELEASE release, but this is more clear
            EventManager.fire(event);
            if (event.isCancelled()) {
                callbackInfo.cancel();
            }
        }
    }

}
