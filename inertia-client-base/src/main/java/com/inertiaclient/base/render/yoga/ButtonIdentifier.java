package com.inertiaclient.base.render.yoga;

import org.lwjgl.glfw.GLFW;

public enum ButtonIdentifier {

    LEFT, RIGHT, MIDDLE, UNKOWN;

    public static ButtonIdentifier fromGLFW(int glfwButton) {
        if (glfwButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return LEFT;
        }
        if (glfwButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            return RIGHT;
        }
        if (glfwButton == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            return MIDDLE;
        }
        return UNKOWN;
    }

}