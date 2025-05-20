package com.inertiaclient.base.utils;

import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static com.inertiaclient.base.InertiaBase.mc;

public class InputUtils {

    public static final InputUtil.Key NO_KEY_BIND = InputUtil.UNKNOWN_KEY;

    private static String getInputLocalizedName(InputUtil.Key input) {
        return input.getLocalizedText().getString();
    }

    public static String getKeybindName(InputUtil.Key input) {
        String inputName = InputUtils.getInputLocalizedName(input);
        if (inputName.equals("")) {
            return "none";
        }

        return inputName;
    }


    public static String getTranslationKey(InputUtil.Key input) {
        //we do -1 because it's less then key.keyboard.unknown, mainly for file management
        /*if (input.getCode() == NO_KEY_BIND.getCode()) {
            return "-1";
        }*/
        return input.getTranslationKey();
    }

    public static InputUtil.Key fromTranslationKey(String translationKey) {
        /*if (translationKey.equals("-1")) {
            return NO_KEY_BIND;
        }*/
        return InputUtil.fromTranslationKey(translationKey);
    }

    public static InputUtil.Key fromKeyCode(int keyCode, int scanCode) {
        return InputUtil.fromKeyCode(keyCode, scanCode);
    }

    public static InputUtil.Key fromMouseButton(int mouseButton) {
        return InputUtil.Type.MOUSE.createFromCode(mouseButton);
    }

    public static InputUtil.Key fromKeyCode(int keyCode) {
        return InputUtil.Type.KEYSYM.createFromCode(keyCode);
    }

    public static boolean isKeyDown(InputUtil.Key input) {
        if (input.getCategory() == InputUtil.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), input.getCode()) == 1;
        }
        return GLFW.glfwGetKey(mc.getWindow().getHandle(), input.getCode()) == 1;
    }

    public static boolean isKeyDown(int keyCode) {
        if (keyCode >= 0 && keyCode <= 7) {
            return GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), keyCode) == 1;
        }
        return GLFW.glfwGetKey(mc.getWindow().getHandle(), keyCode) == 1;
    }

}
