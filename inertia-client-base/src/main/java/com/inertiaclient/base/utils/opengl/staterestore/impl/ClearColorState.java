package com.inertiaclient.base.utils.opengl.staterestore.impl;

import com.inertiaclient.base.utils.opengl.staterestore.OpenGLState;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.Stack;

import static org.lwjgl.opengl.GL11C.GL_COLOR_CLEAR_VALUE;
import static org.lwjgl.opengl.GL11C.glGetFloatv;

public class ClearColorState implements OpenGLState {

    private final Stack<float[]> before = new Stack<>();

    @Override
    public void cache() {
        float[] beforeClearColor = new float[4];
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer clearColor = stack.mallocFloat(4);
            glGetFloatv(GL_COLOR_CLEAR_VALUE, clearColor);
            beforeClearColor[0] = clearColor.get(0);
            beforeClearColor[1] = clearColor.get(1);
            beforeClearColor[2] = clearColor.get(2);
            beforeClearColor[3] = clearColor.get(3);
        }
        this.before.push(beforeClearColor);
    }

    @Override
    public void restore() {
        float[] beforeClearColor = this.before.pop();
        GlStateManager._clearColor(beforeClearColor[0], beforeClearColor[1], beforeClearColor[2], beforeClearColor[3]);
    }
}
