package com.inertiaclient.base.gui.components.friends;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.YogaNode;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;
import lombok.Setter;

import java.awt.Color;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class CachedHeadComponent extends YogaNode {

    @Setter
    private String uuidOrName;
    @Setter
    private boolean isHeadVisible = true;
    @Setter
    private Supplier<Float> blurRadius;

    public CachedHeadComponent(String uuidOrName) {
        this.uuidOrName = uuidOrName;

        this.styleSetWidth(10f);
        this.styleSetHeight(10f);
        this.setShouldScissorChildren(true);

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            if (this.uuidOrName == null) {
                this.renderSpinner(canvas);
                return;
            }

            var skin = InertiaBase.instance.getFileManager().getSkinCache().load(this.uuidOrName);
            if (skin.isDone() && !skin.isCompletedExceptionally()) {
                try {
                    canvas.drawImageRect(skin.get(), Rect.makeXYWH(0, 0, 10, 10), null, this.blurRadius);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }

            } else {
                this.renderSpinner(canvas);
            }
        });
    }

    public CachedHeadComponent() {
        this(null);
    }

    protected boolean implIsVisible() {
        return this.isHeadVisible;
    }

    private void renderSpinner(CanvasWrapper canvas) {
        float radius = 3;
        int rotationsPerSeconds = 1;
        double seconds = System.nanoTime() / 1.0E9;
        double rotation = ((seconds * 360) % 360) * rotationsPerSeconds;
        canvas.save();
        canvas.clipRect(0, 0, this.getWidth(), this.getHeight());
        canvas.translate(this.getWidth() / 2f, this.getHeight() / 2f);
        canvas.rotate((float) rotation);

        try (Paint paint = SkiaUtils.createStrokePaint(Color.white, .5f)) {
            canvas.getCanvas().drawArc(-radius, -radius, radius, radius, -90, 270, false, paint);
        }
        canvas.restore();
    }

}
