package com.inertiaclient.base.utils.opengl.staterestore;

import com.inertiaclient.base.utils.opengl.staterestore.impl.ClearColorState;
import com.inertiaclient.base.utils.opengl.staterestore.impl.IntegerState;
import com.inertiaclient.base.utils.opengl.staterestore.impl.ViewPortState;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class OpenGLStates {

    //many of these use a stack so they can be nested in each other
    public static final IntegerState FRAME_BUFFER = new IntegerState() {
        @Override
        protected int getValue() {
            return GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        }

        @Override
        protected void restoreValue(int value) {
            GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, value);
        }
    };

    public static final ViewPortState VIEW_PORT = new ViewPortState();
    public static final ClearColorState CLEAR_COLOR = new ClearColorState();

}
