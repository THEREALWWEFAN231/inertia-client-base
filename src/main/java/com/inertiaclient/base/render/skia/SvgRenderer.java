package com.inertiaclient.base.render.skia;

import io.github.humbleui.skija.*;
import io.github.humbleui.skija.svg.SVGDOM;
import io.github.humbleui.skija.svg.SVGLength;
import io.github.humbleui.skija.svg.SVGSVG;
import io.github.humbleui.types.Rect;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class SvgRenderer {

    private SVGDOM svgdom;
    private Picture rasteredSvg;

    public SvgRenderer(String svgLocation) {
        try {
            this.svgdom = SkiaUtils.createSvgDom(svgLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void render(CanvasWrapper canvas, float width, float height) {
        this.render(canvas, width, height, null);
    }

    public void render(CanvasWrapper canvas, float width, float height, @Nullable Paint paint) {
        this.render(canvas, 0, 0, width, height, paint);
    }

    public void render(CanvasWrapper canvas, float x, float y, float width, float height) {
        this.render(canvas, x, y, width, height, null);
    }

    public void render(CanvasWrapper canvas, float x, float y, float width, float height, @Nullable Paint paint) {
        if (paint != null) {
            paint.setColorFilter(ColorFilter.makeLighting(paint.getColor(), 0));
        }
        this.renderRawPaint(canvas, x, y, width, height, paint);
    }


    public void renderRawPaint(CanvasWrapper canvas, float x, float y, float width, float height, @Nullable Paint paint) {
        if (this.rasteredSvg == null) {
            this.rasteredSvg = this.rasterSvg(width, height);
        }

        canvas.save();
        canvas.translate(x, y);
        canvas.drawPicture(this.rasteredSvg, null, paint);
        canvas.restore();
    }

    private Picture rasterSvg(float width, float height) {
        try (SVGSVG root = svgdom.getRoot()) {
            root.setWidth(new SVGLength(width)).setHeight(new SVGLength(height));

            try (PictureRecorder pictureRecorder = new PictureRecorder()) {
                Canvas canvas = pictureRecorder.beginRecording(Rect.makeWH(width, height));
                svgdom.render(canvas);
                return pictureRecorder.finishRecordingAsPicture();
            }
        }
    }

}
