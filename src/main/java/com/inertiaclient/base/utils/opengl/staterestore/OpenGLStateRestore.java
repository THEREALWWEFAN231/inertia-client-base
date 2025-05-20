package com.inertiaclient.base.utils.opengl.staterestore;

import com.inertiaclient.base.mixin.custominterfaces.CapabilityTrackerInterface;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferRenderer;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL14C.*;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER_BINDING;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL20C.GL_CURRENT_PROGRAM;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL30C.GL_VERTEX_ARRAY_BINDING;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL31C.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL31C.GL_UNIFORM_BUFFER_BINDING;

//https://github.com/Bubblie01/Merlin-GUI/blob/1.19/src/main/java/io/github/bubblie01/merlingui/MerlinWindow.java
public class OpenGLStateRestore implements AutoCloseable {

    private final int program;
    private final int srcRGB;
    private final int srcAlpha;
    private final int dstRGB;
    private final int dstAlpha;
    private final boolean cullFaceEnabled;
    private final int cullFace;
    private final int frontFace;
    private final boolean blendEnabled;
    private final int blendFunction;
    private final boolean depthTestEnabled;
    private final boolean depthMaskFlag;
    private final boolean scissorTestEnabled;
    private final boolean colorMaskRed;
    private final boolean colorMaskGreen;
    private final boolean colorMaskBlue;
    private final boolean colorMaskAlpha;
    private final int stencilMask;
    private final int sfail;
    private final int dpfail;
    private final int dppass;
    private final int stencilFunc;
    private final int stencilRef;
    private final int stencilValueMask;
    private final int uniformBuffer;
    private final int vertexArray;
    private final int arrayBuffer;
    private final int activeTexture;
    private final int[] boundTextures;
    //private final int texture2D;
    private final int unpackRowLength;
    private final int unpackSkipPixels;
    private final int unpackSkipRows;
    private final int unpackAlignment;

    private float clearColorRed;
    private float clearColorGreen;
    private float clearColorBlue;
    private float clearColorAlpha;

    private float clearDepth;

    public OpenGLStateRestore() {
        this.program = glGetInteger(GL_CURRENT_PROGRAM);
        this.srcRGB = glGetInteger(GL_BLEND_SRC_RGB);
        this.dstRGB = glGetInteger(GL_BLEND_DST_RGB);
        this.srcAlpha = glGetInteger(GL_BLEND_SRC_ALPHA);
        this.dstAlpha = glGetInteger(GL_BLEND_DST_ALPHA);
        this.cullFaceEnabled = glIsEnabled(GL_CULL_FACE);
        this.cullFace = glGetInteger(GL_CULL_FACE_MODE);
        this.frontFace = glGetInteger(GL_FRONT_FACE);
        this.blendEnabled = glIsEnabled(GL_BLEND);
        this.blendFunction = glGetInteger(GL20.GL_BLEND_EQUATION_RGB);
        this.depthTestEnabled = glIsEnabled(GL_DEPTH_TEST);
        this.depthMaskFlag = glGetBoolean(GL_DEPTH_WRITEMASK);
        this.scissorTestEnabled = glIsEnabled(GL_SCISSOR_TEST);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer colorMaskTmp = stack.malloc(4);
            glGetBooleanv(GL_COLOR_WRITEMASK, colorMaskTmp);
            this.colorMaskRed = colorMaskTmp.get(0) != GL_FALSE;
            this.colorMaskGreen = colorMaskTmp.get(1) != GL_FALSE;
            this.colorMaskBlue = colorMaskTmp.get(2) != GL_FALSE;
            this.colorMaskAlpha = colorMaskTmp.get(3) != GL_FALSE;
        }
        this.stencilMask = glGetInteger(GL_STENCIL_WRITEMASK);
        this.sfail = glGetInteger(GL_STENCIL_FAIL);
        this.dpfail = glGetInteger(GL_STENCIL_PASS_DEPTH_FAIL);
        this.dppass = glGetInteger(GL_STENCIL_PASS_DEPTH_PASS);
        this.stencilFunc = glGetInteger(GL_STENCIL_FUNC);
        this.stencilRef = glGetInteger(GL_STENCIL_REF);
        this.stencilValueMask = glGetInteger(GL_STENCIL_VALUE_MASK);
        // make sure we read texture 0, which nanovg binds to
        //RenderSystem.activeTexture(GL_TEXTURE0);

        this.uniformBuffer = glGetInteger(GL_UNIFORM_BUFFER_BINDING);
        this.vertexArray = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        this.arrayBuffer = glGetInteger(GL_ARRAY_BUFFER_BINDING);
        this.activeTexture = GlStateManager.activeTexture;
        this.boundTextures = new int[GlStateManager.TEXTURES.length];
        for (int i = 0; i < GlStateManager.TEXTURES.length; i++) {
            boundTextures[i] = GlStateManager.TEXTURES[i].boundTexture;
        }
        //this.texture2D = glGetInteger(GL_TEXTURE_BINDING_2D);

        this.unpackRowLength = glGetInteger(GL_UNPACK_ROW_LENGTH);
        this.unpackSkipPixels = glGetInteger(GL_UNPACK_SKIP_PIXELS);
        this.unpackSkipRows = glGetInteger(GL_UNPACK_SKIP_ROWS);
        this.unpackAlignment = glGetInteger(GL_UNPACK_ALIGNMENT);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer colorMaskTmp = stack.mallocFloat(4);
            glGetFloatv(GL_COLOR_CLEAR_VALUE, colorMaskTmp);
            this.clearColorRed = colorMaskTmp.get(0);
            this.clearColorGreen = colorMaskTmp.get(1);
            this.clearColorBlue = colorMaskTmp.get(2);
            this.clearColorAlpha = colorMaskTmp.get(3);
        }

        this.clearDepth = glGetFloat(GL_DEPTH_CLEAR_VALUE);
    }

    @Override
    public void close() {
        glUseProgram(program);
        forceSetBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
        //is this correct?
        forceSetBlendFunc(srcRGB, dstRGB);
        ((CapabilityTrackerInterface) GlStateManager.CULL.capState).forceSetState(cullFaceEnabled);
        glCullFace(cullFace);
        glFrontFace(frontFace);
        ((CapabilityTrackerInterface) GlStateManager.BLEND.capState).forceSetState(blendEnabled);
        GL14.glBlendEquation(blendFunction);
        ((CapabilityTrackerInterface) GlStateManager.DEPTH.capState).forceSetState(depthTestEnabled);
        forceSetDepthMask(depthMaskFlag);
        ((CapabilityTrackerInterface) GlStateManager.SCISSOR.capState).forceSetState(scissorTestEnabled);
        forceSetColorMask(colorMaskRed, colorMaskGreen, colorMaskBlue, colorMaskAlpha);
        forceSetStencilMask(stencilMask);
        forceSetStencilOp(sfail, dpfail, dppass);
        forceSetStencilFunc(stencilFunc, stencilRef, stencilValueMask);

        for (int i = 0; i < this.boundTextures.length; i++) {
            GlStateManager.TEXTURES[i].boundTexture = this.boundTextures[i];
            GL13.glActiveTexture(GL_TEXTURE0 + i);
            GL11.glBindTexture(GlConst.GL_TEXTURE_2D, this.boundTextures[i]);
        }
        GlStateManager.activeTexture = this.activeTexture;
        GL13.glActiveTexture(GL_TEXTURE0 + GlStateManager.activeTexture);

        glBindBuffer(GL_UNIFORM_BUFFER, uniformBuffer);
        //VertexBuffer.bind/unbind does this, its "needed" when glBindVertexArray changes
        BufferRenderer.resetCurrentVertexBuffer();
        glBindVertexArray(vertexArray);
        glBindBuffer(GL_ARRAY_BUFFER, arrayBuffer);
        //forceSetBindTexture(texture2D);
        //glUniformBlockBinding(... , GLNVG_FRAG_BINDING); TODO: not used in vanilla, but might be elsewhere

        //minecraft doesn't do any caching of these so no need to "force" change anything
        GlStateManager._pixelStore(GL_UNPACK_ROW_LENGTH, this.unpackRowLength);
        GlStateManager._pixelStore(GL_UNPACK_SKIP_PIXELS, this.unpackSkipPixels);
        GlStateManager._pixelStore(GL_UNPACK_SKIP_ROWS, this.unpackSkipRows);
        GlStateManager._pixelStore(GL_UNPACK_ALIGNMENT, this.unpackAlignment);

        glClearColor(clearColorRed, clearColorGreen, clearColorBlue, clearColorAlpha);
        glClearDepth(this.clearDepth);
    }

    public static void forceSetBlendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
        GlStateManager.BLEND.srcFactorRGB = srcFactorRGB;
        GlStateManager.BLEND.dstFactorRGB = dstFactorRGB;
        GlStateManager.BLEND.srcFactorAlpha = srcFactorAlpha;
        GlStateManager.BLEND.dstFactorAlpha = dstFactorAlpha;
        GlStateManager.glBlendFuncSeparate(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
    }

    public static void forceSetBlendFunc(int srcFactorRGB, int dstFactorRGB) {
        GlStateManager.BLEND.srcFactorRGB = srcFactorRGB;
        GlStateManager.BLEND.dstFactorRGB = dstFactorRGB;
        GL11.glBlendFunc(srcFactorRGB, dstFactorRGB);
    }

    public static void forceSetDepthMask(boolean mask) {
        GlStateManager.DEPTH.mask = mask;
        GL11.glDepthMask(mask);
    }

    public static void forceSetColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        GlStateManager.COLOR_MASK.red = red;
        GlStateManager.COLOR_MASK.green = green;
        GlStateManager.COLOR_MASK.blue = blue;
        GlStateManager.COLOR_MASK.alpha = alpha;
        GL11.glColorMask(red, green, blue, alpha);
    }

    public static void forceSetStencilMask(int mask) {
        GlStateManager.STENCIL.mask = mask;
        GL11.glStencilMask(mask);
    }

    public static void forceSetStencilOp(int sfail, int dpfail, int dppass) {
        GlStateManager.STENCIL.sfail = sfail;
        GlStateManager.STENCIL.dpfail = dpfail;
        GlStateManager.STENCIL.dppass = dppass;
        GL11.glStencilOp(sfail, dpfail, dppass);
    }

    public static void forceSetStencilFunc(int func, int ref, int mask) {
        GlStateManager.STENCIL.subState.func = func;
        GlStateManager.STENCIL.subState.ref = ref;
        GlStateManager.STENCIL.subState.mask = mask;
        GL11.glStencilFunc(func, ref, mask);
    }

    public static void forceSetBindTexture(int texture) {
        GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture = texture;
        GL11.glBindTexture(GlConst.GL_TEXTURE_2D, texture);
    }

    public static void glSetEnabled(int cap, boolean state) {
        if (state) {
            glEnable(cap);
        } else {
            glDisable(cap);
        }
    }
}