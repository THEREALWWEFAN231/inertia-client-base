package com.inertiaclient.base.utils;

import lombok.AllArgsConstructor;
import org.lwjgl.sdl.SDLMouse;

import java.util.HashMap;

public class CursorUtils {

    private static final HashMap<Integer, Long> cursors = new HashMap<>();
    private static long currentCursor = -1;

    public static void setCursor(Cursor cursor) {
        CursorUtils.setCursor(cursor.glfwCursorId);
    }

    public static void setCursor(int glfwCursor) {
        if (currentCursor == glfwCursor) {
            return;
        }

        long cursor = -1;
        if (cursors.containsKey(glfwCursor)) {
            cursor = cursors.get(glfwCursor);
        } else {
            cursor = SDLMouse.SDL_CreateSystemCursor(glfwCursor);
            cursors.put(glfwCursor, cursor);
        }
        SDLMouse.SDL_SetCursor(cursor);
        currentCursor = glfwCursor;
    }

    @AllArgsConstructor
    public enum Cursor {
        //TODO: are these correct? from glfw -> sdl
        ARROW(SDLMouse.SDL_SYSTEM_CURSOR_DEFAULT), HAND(SDLMouse.SDL_SYSTEM_CURSOR_POINTER), IBEAM(SDLMouse.SDL_SYSTEM_CURSOR_TEXT), RESIZE_ALL(SDLMouse.SDL_SYSTEM_CURSOR_MOVE);

        private final int glfwCursorId;
    }

}
