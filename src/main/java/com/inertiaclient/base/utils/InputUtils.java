package com.inertiaclient.base.utils;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

import static com.inertiaclient.base.InertiaBase.mc;

public class InputUtils {

    public static final InputConstants.Key NO_KEY_BIND = InputConstants.UNKNOWN;

    private static String getInputLocalizedName(InputConstants.Key input) {
        return input.getDisplayName().getString();
    }

    public static String getKeybindName(InputConstants.Key input) {
        String inputName = InputUtils.getInputLocalizedName(input);
        if (inputName.equals("")) {
            return "none";
        }

        return inputName;
    }


    public static String getTranslationKey(InputConstants.Key input) {
        //we do -1 because it's less then key.keyboard.unknown, mainly for file management
        /*if (input.getCode() == NO_KEY_BIND.getCode()) {
            return "-1";
        }*/
        return input.getName();
    }

    public static InputConstants.Key fromTranslationKey(String translationKey) {
        /*if (translationKey.equals("-1")) {
            return NO_KEY_BIND;
        }*/
        return InputConstants.getKey(translationKey);
    }

    public static InputConstants.Key fromKeyCode(int keyCode, int scanCode) {
        return InputConstants.getKey(keyCode, scanCode);
    }

    public static InputConstants.Key fromMouseButton(int mouseButton) {
        return InputConstants.Type.MOUSE.getOrCreate(mouseButton);
    }

    public static InputConstants.Key fromKeyCode(int keyCode) {
        return InputConstants.Type.KEYSYM.getOrCreate(keyCode);
    }

    public static boolean isKeyDown(InputConstants.Key input) {
        if (input.getType() == InputConstants.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), input.getValue()) == 1;
        }
        return GLFW.glfwGetKey(mc.getWindow().getWindow(), input.getValue()) == 1;
    }

    public static boolean isKeyDown(int keyCode) {
        if (keyCode >= 0 && keyCode <= 7) {
            return GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), keyCode) == 1;
        }
        return GLFW.glfwGetKey(mc.getWindow().getWindow(), keyCode) == 1;
    }

}
