package com.inertiaclient.base.render.skia;

import com.inertiaclient.base.render.skia.instances.SkiaVulkanInstance;
import io.github.humbleui.skija.Surface;
import net.minecraft.util.Mth;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/*
AI suggested this, and made most of this
it suggested a texture atlas for caching skia renders or separate surfaces to snapshot/cache an image, that is what I went for
if we use a screen size surface to cache components, and make a snapshot/cache it(even with IRect) method, it copies the whole image, no just the bounds we specified
which will use a lot of gpu ram
so what we do is create a surface at the size we need and save it for future use, when we snapshot/cache it, skia copies the content of the surface into a rasterized image
we are using a ArrayDeque instead of just keeping one surface for one size, in case two things need to draw into the same size at the same time, it will create two separate surfaces
for example water mark might be cached at 50x50, while that's happening svg renderer needs to rasterize the watermark at 50x50, or something like that...
this seems to cause crashes with no log of why, maybe because of a leak, I'm not sure
 */

//this shouldn't really be used yet, is more than likely unreliable
public class SurfacePool implements AutoCloseable {

    public static final SurfacePool INSTANCE = new SurfacePool();

    private record Size(int width, int height) {

    }

    private final Map<Size, ArrayDeque<Surface>> pool = new HashMap<>();
    private boolean closed;

    public Surface acquire(float width, float height) {
        return this.acquire(Mth.ceil(width), Mth.ceil(height));
    }

    public Surface acquire(int width, int height) {
        if (closed) {
            throw new IllegalStateException("SurfacePool is closed");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid surface dimensions");
        }

        Size size = new Size(width, height);
        ArrayDeque<Surface> surfaces = this.pool.get(size);

        if (surfaces != null && !surfaces.isEmpty()) {
            return surfaces.pop();
        }

        return SkiaVulkanInstance.createGPUSurface(width, height);
    }

    public void release(Surface surface) {
        if (closed) {
            surface.close();
            return;
        }

        int width = surface.getWidth();
        int height = surface.getHeight();

        surface.getCanvas().clear(0x00000000);

        pool.computeIfAbsent(new Size(width, height), ignored -> new ArrayDeque<>()).push(surface);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        closed = true;

        for (ArrayDeque<Surface> surfaces : pool.values()) {
            for (Surface surface : surfaces) {
                surface.close();
            }
        }

        pool.clear();
    }
}