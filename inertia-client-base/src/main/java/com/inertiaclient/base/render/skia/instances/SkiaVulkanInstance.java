package com.inertiaclient.base.render.skia.instances;

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
import com.inertiaclient.base.render.GenericRender;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.utils.UIUtils;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.backend.vulkan.VulkanConst;
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

import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;

//https://github.com/HumbleUI/Skija/blob/master/examples/vulkan/src/Main.java
public class SkiaVulkanInstance extends SkiaInstance {

    @Getter
    private static DirectContext skiaDirectContext;
    @Getter
    private static HashMap<Long, Image> skiaNativeImages = new HashMap<>();

    private CachedFrameBuffer.TwoDDCachedFrameBuffer frameBuffer;
    private GenericRender skiaDraw;
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

    private SkiaVulkanInstance(int width, int height, GenericRender skiaDraw) {
        this.frameBuffer = new CachedFrameBuffer.TwoDDCachedFrameBuffer();
        this.frameBuffer.setRenderer((minecraftGraphics, mouseX, mouseY, delta) -> {
            SkiaVulkanInstance.skiaDirectContext.resetAll();
            this.canvas.clear(0x00000000);
            this.skiaDraw.render(minecraftGraphics, mouseX, mouseY, delta);
            SkiaVulkanInstance.skiaDirectContext.flush();
        });
        this.resize(width, height);
        this.skiaDraw = skiaDraw;

        EventManager.register(this);
    }

    public SkiaVulkanInstance(GenericRender drawWithSkia) {
        this(InertiaBase.mc.getWindow().getWidth(), InertiaBase.mc.getWindow().getHeight(), drawWithSkia);
    }

    public void resize(int width, int height) {
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

        float scale = UIUtils.getScaleFactor();
        //this.canvas.scale(1/scale, 1/scale);
        this.canvas.scale(scale, scale);
    }

    public void setFps(Supplier<Integer> fps) {
        this.frameBuffer.setFps(fps);
    }

    public void drawAndRender(GuiGraphicsExtractor graphics, float mouseX, float mouseY, float delta) {
        this.frameBuffer.drawWithRenderer(graphics, mouseX, mouseY, delta);
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

    public static Image createNativeImage(TextureTarget from) {
        var colorTexture = (VulkanGpuTexture) from.getColorTexture();
        var skiaImageInfo = new VkImageInfo(colorTexture.vkImage(), new VulkanAlloc(VK_NULL_HANDLE, VK_NULL_HANDLE, VK_NULL_HANDLE, VK_NULL_HANDLE), 0, 0, VulkanConst.toVk(from.getColorTexture().getFormat()), 15, 1, from.getColorTexture().getMipLevels(), -1, false, 0);
        return Image.borrowTextureFrom(SkiaVulkanInstance.getSkiaDirectContext(), BackendTexture.makeVulkan(from.width, from.height, skiaImageInfo), SurfaceOrigin.BOTTOM_LEFT, ColorType.RGBA_8888, ColorAlphaType.PREMUL, null, null);
    }

    public void onEvent(ResolutionChangeEvent event) {
        if (event.getType() == ResolutionChangeEvent.Type.POST) {
            this.resize(InertiaBase.mc.getWindow().getWidth(), InertiaBase.mc.getWindow().getHeight());
        }
    }

    private static FrontendGpuDeviceAccessor getFrontendDevice() {
        return (FrontendGpuDeviceAccessor) RenderSystem.getDevice();
    }

}
