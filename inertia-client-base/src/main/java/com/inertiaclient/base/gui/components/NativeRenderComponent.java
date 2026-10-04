package com.inertiaclient.base.gui.components;

import com.inertiaclient.base.render.skia.instances.SkiaNativeRender;
import com.inertiaclient.base.render.yoga.YogaNode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.function.Consumer;

public class NativeRenderComponent extends YogaNode {

    @Getter
    private SkiaNativeRender skiaNativeRender;
    @Accessors(chain = true)
    @Setter
    private RenderCallback beforeNativeRenderCallback;
    @Accessors(chain = true)
    @Setter
    private RenderCallback afterNativeRenderCallback;

    public NativeRenderComponent(Consumer<GuiGraphicsExtractor> setNativeRender) {
        this(new SkiaNativeRender().setSetNativeRender(setNativeRender));
    }

    public NativeRenderComponent(SkiaNativeRender nativeRender) {
        this.skiaNativeRender = nativeRender;
        nativeRender.setNativeWidth(this::getWidth);
        nativeRender.setNativeHeight(this::getHeight);

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            this.doRenderCallback(this.beforeNativeRenderCallback, context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);
            nativeRender.update();
            nativeRender.drawImageWithSkia(canvas, 0, 0);
            this.doRenderCallback(this.afterNativeRenderCallback, context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas);
        });
    }

}
