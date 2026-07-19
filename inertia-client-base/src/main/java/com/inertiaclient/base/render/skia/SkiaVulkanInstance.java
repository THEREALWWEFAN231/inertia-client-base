package com.inertiaclient.base.render.skia;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl.ResolutionChangeEvent;
import com.inertiaclient.base.mixin.custominterfaces.VulkanDeviceInterface;
import com.inertiaclient.base.mixin.mixins.accessors.FrontendGpuDeviceAccessor;
import com.inertiaclient.base.mixin.mixins.accessors.FrontendGpuSurfaceAccessor;
import com.inertiaclient.base.mixin.mixins.accessors.VulkanGpuSurfaceAccessor;
import com.inertiaclient.base.render.CachedFrameBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.backend.vulkan.VulkanDevice;
import com.mojang.renderpearl.backend.vulkan.VulkanGpuTexture;
import io.github.humbleui.skija.*;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK11;

import java.util.HashMap;
import java.util.function.Supplier;

//https://github.com/HumbleUI/Skija/blob/master/examples/vulkan/src/Main.java
public class SkiaVulkanInstance {

    @Getter
    private static DirectContext skiaDirectContext;
    @Getter
    private static HashMap<Long, Image> skiaNativeImages = new HashMap<>();

    private CachedFrameBuffer frameBuffer;
    @Getter
    private Surface surface;
    @Getter
    private Canvas canvas;
    @Getter
    private int width;
    @Getter
    private int height;
    @Getter
    private CanvasWrapper canvasWrapper;
    private BackendRenderTarget renderTarget;
    private static int queueFamilyIndex;

    @EventTarget
    private final EventListener<ResolutionChangeEvent> resolutionChangeListener = this::onEvent;

    private SkiaVulkanInstance(int width, int height) {
        this.frameBuffer = new CachedFrameBuffer();
        this.resize(width, height);

        EventManager.register(this);
    }

    public SkiaVulkanInstance() {
        this(InertiaBase.mc.getWindow().getWidth(), InertiaBase.mc.getWindow().getHeight());
    }

    public void resize(int width, int height) {
        if (!(((FrontendGpuDeviceAccessor) RenderSystem.getDevice()).getBackend() instanceof VulkanDevice)) {
            InertiaBase.LOGGER.error("Not using vulkan resize");
            return;
        }

        this.width = width;
        this.height = height;

        if (this.renderTarget != null) {
            this.renderTarget.close();
            this.renderTarget = null;
        }
        if (this.surface != null) {
            this.surface.close();
            this.surface = null;
        }
        if (this.frameBuffer.getFramebuffer() != null) {
            this.frameBuffer.resize(this.width, this.height);
        } else {
            this.frameBuffer.createFrameBufferIfNeeded(this.width, this.height, false, false);
        }

        VulkanGpuSurfaceAccessor vulkanGpuSurface = (VulkanGpuSurfaceAccessor) ((FrontendGpuSurfaceAccessor) InertiaBase.mc.windowSurface()).getBackend();
        ColorType colorType = ColorType.BGRA_8888;
        if (vulkanGpuSurface.getSwapchainImageFormat() == VK10.VK_FORMAT_R8G8B8A8_UNORM || vulkanGpuSurface.getSwapchainImageFormat() == VK10.VK_FORMAT_R8G8B8A8_SRGB) {
            colorType = ColorType.RGBA_8888;
        }

        this.renderTarget = BackendRenderTarget.makeVulkan(this.width, this.height, ((VulkanGpuTexture) this.frameBuffer.getFramebuffer().getColorTexture()).vkImage(), VK10.VK_IMAGE_TILING_OPTIMAL, VK10.VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, VK10.VK_FORMAT_R8G8B8A8_UNORM, VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK10.VK_IMAGE_USAGE_SAMPLED_BIT | VK10.VK_IMAGE_USAGE_TRANSFER_SRC_BIT, 1, 1);
        // TODO load monitor profile
        this.surface = Surface.wrapBackendRenderTarget(SkiaVulkanInstance.skiaDirectContext, this.renderTarget, SurfaceOrigin.TOP_LEFT, colorType, ColorSpace.getDisplayP3(), new SurfaceProps(PixelGeometry.RGB_H));
        this.canvas = this.surface.getCanvas();
        this.canvasWrapper = new CanvasWrapper(this.canvas, this);

        float scale = SkiaVulkanInstance.getScaleFactor();
        //this.canvas.scale(1/scale, 1/scale);
        this.canvas.scale(scale, scale);
    }

    public void setFps(Supplier<Integer> fps) {
        this.frameBuffer.setFps(fps);
    }

    public void setup(GuiGraphicsExtractor graphics, Runnable draw) {

        this.frameBuffer.setRenderer(() -> {
            SkiaVulkanInstance.skiaDirectContext.resetAll();
            this.canvas.clear(0x00000000);
            draw.run();
            SkiaVulkanInstance.skiaDirectContext.flush();

        });
        this.frameBuffer.drawWithRenderer();
        this.frameBuffer.renderCachedImage(graphics);
    }


    public static void makeDirectContext() {
        long instanceProcAddr = VK.getFunctionProvider().getFunctionAddress("vkGetInstanceProcAddr");
        long deviceProcAddr = VK.getFunctionProvider().getFunctionAddress("vkGetDeviceProcAddr");

        VulkanDevice vulkanDevice = (VulkanDevice) getFrontendDevice().getBackend();
        var physicalDevice = ((VulkanDeviceInterface) vulkanDevice).getVulkanPhysicalDevice().vkPhysicalDevice();
        queueFamilyIndex = vulkanDevice.graphicsQueue().queueFamilyIndex();
        skiaDirectContext = DirectContext.makeVulkan(vulkanDevice.instance().vkInstance().address(), physicalDevice.address(), vulkanDevice.vkDevice().address(), vulkanDevice.graphicsQueue().vkQueue().address(), /*idk.firstInt()*/ queueFamilyIndex, instanceProcAddr, deviceProcAddr, VK11.VK_API_VERSION_1_1);
    }

    public void onEvent(ResolutionChangeEvent event) {
        if (event.getType() == ResolutionChangeEvent.Type.POST) {
            this.resize(InertiaBase.mc.getWindow().getWidth(), InertiaBase.mc.getWindow().getHeight());
        }
    }

    public static float getScaleFactor() {
        return (float) InertiaBase.mc.getWindow().getGuiScale();
    }

    private static FrontendGpuDeviceAccessor getFrontendDevice() {
        return (FrontendGpuDeviceAccessor) RenderSystem.getDevice();
    }

}
