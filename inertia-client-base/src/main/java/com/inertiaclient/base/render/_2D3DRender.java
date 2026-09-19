package com.inertiaclient.base.render;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl.Skia2D3DEvent;
import com.inertiaclient.base.event.impl._2D3DEvent;
import com.inertiaclient.base.mixin.custominterfaces.GameRendererInterface;
import com.inertiaclient.base.render.skia.instances.SkiaInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class _2D3DRender {

    private static SkiaInstance skiaInstance;

    public static void render(float tickDelta) {
        CoordinateDimensionTranslator.setupOverlayRendering((graphicsExtractor) -> {
            render(tickDelta, graphicsExtractor);
        });
    }

    //so by default we render after the world but before the players hand, so we are rendering 2d, but under the players hand, so it looks like it's in the world, but iris does something, I don't know what. So when iris and shaders are enabled we render over the hand, InGameHudMixin
    private static void render(float tickDelta, GuiGraphicsExtractor graphics) {
        if (skiaInstance == null) {
            skiaInstance = SkiaInstance.create((graphics1, mouseX, mouseY, delta) -> {
                EventManager.fire(new Skia2D3DEvent(skiaInstance.getCanvasWrapper(), graphics1, tickDelta));
            });
            skiaInstance.setFps(() -> InertiaBase.instance.getSettings().getWorldEspFPS().getFpsForCache());
        }
        EventManager.fire(new _2D3DEvent(graphics, tickDelta));
        ((GameRendererInterface) InertiaBase.mc.gameRenderer).get3DCachedFrameBuffer().renderCachedImage(graphics);

        skiaInstance.drawAndRender(graphics, -999, -999, tickDelta);
    }

}
