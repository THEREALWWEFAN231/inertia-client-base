package com.inertiaclient.base.render;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.Skia2D3DEvent;
import com.inertiaclient.base.event.impl._2D3DEvent;
import com.inertiaclient.base.render.skia.SkiaOpenGLInstance;
import com.inertiaclient.base.utils.opengl.CoordinateDimensionTranslator;
import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.Iris;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class _2D3DRender {

    private static SkiaOpenGLInstance skiaInstance;

    public static void render(float tickDelta, MatrixStack matrices, boolean callFromHud) {
        //TODO: make isModLoaded static somewhere
        boolean isUsingShaders = FabricLoader.getInstance().isModLoaded("iris") && Iris.isPackInUseQuick();
        if (callFromHud) {
            if (isUsingShaders) {
                render(tickDelta, matrices);
            }
        } else if (!isUsingShaders) {
            CoordinateDimensionTranslator.setupOverlayRendering(() -> {
                render(tickDelta, matrices);
            });
        }
    }

    //so by default we render after the world but before the players hand, so we are rendering 2d, but under the players hand, so it looks like it's in the world, but iris does something, I don't know what. So when iris and shaders are enabled we render over the hand, InGameHudMixin
    private static void render(float tickDelta, MatrixStack matrices) {
        if (skiaInstance == null) {
            skiaInstance = new SkiaOpenGLInstance();
        }

        DrawContext drawContext = new DrawContext(InertiaBase.mc, InertiaBase.mc.getBufferBuilders().getEntityVertexConsumers());

        EventManager.fire(new _2D3DEvent(drawContext, tickDelta));

        //c.setFps(120);
        skiaInstance.setup(() -> {
            EventManager.fire(new Skia2D3DEvent(skiaInstance.getCanvasWrapper(), drawContext, matrices, tickDelta));
        });
        drawContext.draw();
    }

}
