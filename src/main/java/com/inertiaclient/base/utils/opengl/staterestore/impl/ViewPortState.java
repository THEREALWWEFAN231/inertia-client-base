package com.inertiaclient.base.utils.opengl.staterestore.impl;

import com.inertiaclient.base.utils.opengl.staterestore.OpenGLState;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Stack;

import static org.lwjgl.opengl.GL11C.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11C.glGetIntegerv;

public class ViewPortState implements OpenGLState {
    
    private final Stack<int[]> before = new Stack<>();

    @Override
    public void cache() {
        int[] beforeViewPortSize = new int[4];
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer viewport = stack.mallocInt(4);
            glGetIntegerv(GL_VIEWPORT, viewport);
            beforeViewPortSize[0] = viewport.get(0);
            beforeViewPortSize[1] = viewport.get(1);
            beforeViewPortSize[2] = viewport.get(2);
            beforeViewPortSize[3] = viewport.get(3);
        }
        this.before.push(beforeViewPortSize);
    }

    @Override
    public void restore() {
        int[] beforeViewPort = this.before.pop();
        GlStateManager._viewport(beforeViewPort[0], beforeViewPort[1], beforeViewPort[2], beforeViewPort[3]);
    }
}
