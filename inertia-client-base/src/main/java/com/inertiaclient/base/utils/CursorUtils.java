package com.inertiaclient.base.utils;

import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

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
            cursor = GLFW.glfwCreateStandardCursor(glfwCursor);
            cursors.put(glfwCursor, cursor);
        }
        GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), cursor);
        currentCursor = glfwCursor;
    }

    @AllArgsConstructor
    public enum Cursor {
        ARROW(GLFW.GLFW_ARROW_CURSOR), HAND(GLFW.GLFW_HAND_CURSOR), IBEAM(GLFW.GLFW_IBEAM_CURSOR), RESIZE_ALL(GLFW.GLFW_RESIZE_ALL_CURSOR);

        private final int glfwCursorId;
    }

}
