package com.inertiaclient.base.render.skia;

import com.inertiaclient.base.render.skia.instances.SkiaVulkanInstance;
import com.inertiaclient.base.utils.InputUtils;
import com.inertiaclient.base.utils.UIUtils;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.Rect;
import net.minecraft.util.Mth;
import org.lwjgl.sdl.SDLScancode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

//this shouldn't really be used yet, is more than likely unreliable
public class CachedRender {

    private boolean needsUpdate = false;

    private float cachedWidth;
    private float cachedHeight;
    private int cachedImageWidth;
    private int cachedImageHeight;

    private Image cachedImage;

    private int cachedImagePadding = 12;

    public void render(float width, float height, Consumer<CanvasWrapper> render, CanvasWrapper renderTo, Paint imagePaint) {
        if (InputUtils.isScancodePressed(SDLScancode.SDL_SCANCODE_Z)) {
            render.accept(renderTo);
            return;
        }


        float[] boundsOnScreen = CachedRender.getTransformedBounds(renderTo.getCanvas().getLocalToDeviceAsMatrix33(), 0, 0, width, height);
        int imageWidth = Mth.ceil(boundsOnScreen[2]);
        int imageHeight = Mth.ceil(boundsOnScreen[3]);

        if (this.cachedWidth != width || this.cachedHeight != height || this.cachedImageWidth != imageWidth || this.cachedImageHeight != imageHeight) {
            this.needsUpdate = true;
        }

        if (InputUtils.isScancodePressed(SDLScancode.SDL_SCANCODE_LSHIFT)) {
            needsUpdate = true;
        }

        if (this.needsUpdate) {
            this.needsUpdate = false;
            this.cache(boundsOnScreen, width, height, imageWidth, imageHeight, render, renderTo);
        }
        renderTo.getCanvas().drawImageRect(this.cachedImage, Rect.makeXYWH(-(this.cachedImagePadding / 2f) / UIUtils.getScaleFactor(), -(this.cachedImagePadding / 2f) / UIUtils.getScaleFactor(), width + ((float) this.cachedImagePadding / UIUtils.getScaleFactor()), height + ((float) this.cachedImagePadding / UIUtils.getScaleFactor())), imagePaint);

        if (InputUtils.isScancodePressed(SDLScancode.SDL_SCANCODE_LALT)) {
            try (Data data = EncoderPNG.encode(SkiaVulkanInstance.getSkiaDirectContext(), this.cachedImage, EncodePNGOptions.DEFAULT)) {


                if (data != null) {
                    byte[] bytes = data.getBytes();
                    try {
                        Files.write(Paths.get("D://users/THEREALWWEFAN/downloads/snapshot.png"), bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void cache(float[] boundsOnScreen, float width, float height, int imageWidth, int imageHeight, Consumer<CanvasWrapper> render, CanvasWrapper renderTo) {
        this.cachedWidth = width;
        this.cachedHeight = height;
        this.cachedImageWidth = imageWidth;
        this.cachedImageHeight = imageHeight;

        var surface = SurfacePool.INSTANCE.acquire(imageWidth + this.cachedImagePadding, imageHeight + this.cachedImagePadding);
        try {
            CanvasWrapper canvas = new CanvasWrapper(surface.getCanvas(), renderTo.getSkiaInstance());
            var renderToMatrix = renderTo.getCanvas().getLocalToDeviceAsMatrix33();

            canvas.getCanvas().setMatrix(Matrix33.makeTranslate(-boundsOnScreen[0], -boundsOnScreen[1]).makeConcat(renderToMatrix));
            canvas.save();
            canvas.translate((this.cachedImagePadding / 2f) / UIUtils.getScaleFactor(), (this.cachedImagePadding / 2f) / UIUtils.getScaleFactor());
            render.accept(canvas);
            canvas.restore();

            if (this.cachedImage != null) {
                this.cachedImage.close();
            }

            this.cachedImage = surface.makeImageSnapshot();
        } finally {
            SurfacePool.INSTANCE.release(surface);
        }
    }

    //chat gbt gave me this
    public static float[] getTransformedBounds(Matrix33 matrix, float x, float y, float width, float height) {
        float[] m = matrix.getMat();

        float[] xs = {x, x + width, x + width, x};
        float[] ys = {y, y, y + height, y + height};

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < 4; i++) {
            float w = m[6] * xs[i] + m[7] * ys[i] + m[8];

            float tx = (m[0] * xs[i] + m[1] * ys[i] + m[2]) / w;
            float ty = (m[3] * xs[i] + m[4] * ys[i] + m[5]) / w;

            minX = Math.min(minX, tx);
            minY = Math.min(minY, ty);
            maxX = Math.max(maxX, tx);
            maxY = Math.max(maxY, ty);
        }

        return new float[]{minX, minY, maxX - minX, maxY - minY};
    }

}
