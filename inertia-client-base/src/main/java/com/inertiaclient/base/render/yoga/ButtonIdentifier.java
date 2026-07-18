package com.inertiaclient.base.render.yoga;

import org.lwjgl.sdl.SDLMouse;

public enum ButtonIdentifier {

    LEFT, RIGHT, MIDDLE, UNKOWN;

    public static ButtonIdentifier fromGLFW(int glfwButton) {
        if (glfwButton == SDLMouse.SDL_BUTTON_LEFT) {
            return LEFT;
        }
        if (glfwButton == SDLMouse.SDL_BUTTON_RIGHT) {
            return RIGHT;
        }
        if (glfwButton == SDLMouse.SDL_BUTTON_MIDDLE) {
            return MIDDLE;
        }
        return UNKOWN;
    }

}