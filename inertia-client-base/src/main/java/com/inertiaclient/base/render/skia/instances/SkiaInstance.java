package com.inertiaclient.base.render.skia.instances;

import com.inertiaclient.base.mixin.mixins.accessors.FrontendGpuDeviceAccessor;
import com.inertiaclient.base.render.GenericRender;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.backend.vulkan.VulkanDevice;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Surface;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.function.Supplier;

public abstract class SkiaInstance {

    public abstract Surface getSurface();

    public abstract Canvas getCanvas();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract CanvasWrapper getCanvasWrapper();

    public abstract void drawAndRender(GuiGraphicsExtractor graphics, float mouseX, float mouseY, float delta);

    public abstract void setFps(Supplier<Integer> fps);

    public static SkiaInstance create(GenericRender drawWithSkia) {
        if (((FrontendGpuDeviceAccessor) RenderSystem.getDevice()).getBackend() instanceof VulkanDevice) {
            return new SkiaVulkanInstance(drawWithSkia);
        }
        return new SkiaOpenGLInstance(drawWithSkia);
    }

}
