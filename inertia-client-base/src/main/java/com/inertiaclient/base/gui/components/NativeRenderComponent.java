package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.render.skia.SkiaNativeRender;
import com.inertiaclient.base.render.yoga.YogaNode;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NativeRenderComponent extends YogaNode {

    public NativeRenderComponent(Supplier<Float> nativeWidth, Supplier<Float> nativeHeight, Consumer<GuiGraphics> setNativeRender) {
        this(new SkiaNativeRender().setNativeWidth(nativeWidth).setNativeHeight(nativeHeight).setSetNativeRender(setNativeRender));
    }

    public NativeRenderComponent(SkiaNativeRender nativeRender) {
        this.setPreLayoutCalculationsCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {

            nativeRender.update(context);
            this.styleSetWidth(nativeRender.getCachedNativeWidth());
            this.styleSetHeight(nativeRender.getCachedNativeHeight());
            //
        });
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            //nativeRender.update(context);
            nativeRender.drawImageWithSkia(canvas, 0, 0);
        });
    }

}
