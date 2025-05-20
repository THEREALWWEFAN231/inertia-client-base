package com.inertiaclient.base.render.skia;

import com.inertiaclient.base.gui.components.MainFrame;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

@AllArgsConstructor
public class CanvasWrapper {

    @Getter
    private Canvas canvas;
    @Getter
    private SkiaOpenGLInstance skiaInstance;

    private static final TextBuilder TEXT_BUILDER = new TextBuilder();

    public static TextBuilder getFreshTextBuilder() {
        TEXT_BUILDER.setDefaults();
        return TEXT_BUILDER;
    }

    public int save() {
        return canvas.save();
    }

    public CanvasWrapper restore() {
        canvas.restore();
        return this;
    }

    public CanvasWrapper translate(float dx, float dy) {
        this.canvas.translate(dx, dy);
        return this;
    }

    public CanvasWrapper scale(float sx, float sy) {
        this.canvas.scale(sx, sy);
        return this;
    }

    public CanvasWrapper rotate(float deg) {
        this.canvas.rotate(deg);
        return this;
    }

    public CanvasWrapper drawPicture(@NotNull Picture picture) {
        this.canvas.drawPicture(picture);
        return this;
    }

    public CanvasWrapper drawPicture(@NotNull Picture picture, @Nullable Matrix33 matrix, @Nullable Paint paint) {
        this.canvas.drawPicture(picture, matrix, paint);
        return this;
    }

    public void drawNativeImage(int textureId, float x, float y, float width, float height) {
        this.drawNativeImage(textureId, x, y, width, height, (int) width, (int) height);
    }

    public void drawNativeImage(int textureId, float x, float y, float width, float height, int imageWidth, int imageHeight) {
        Image image = SkiaOpenGLInstance.getSkiaNativeImages().get(textureId);
        if (image == null) {
            image = Image.adoptGLTextureFrom(SkiaOpenGLInstance.getSkiaDirectContext(), textureId, GL11.GL_TEXTURE_2D, imageWidth, imageHeight, GL11.GL_RGBA8, SurfaceOrigin.BOTTOM_LEFT, ColorType.RGBA_8888);
            SkiaOpenGLInstance.getSkiaNativeImages().put(textureId, image);
        }
        this.canvas.drawImageRect(image, Rect.makeXYWH(x, y, width, height));
    }


    public CanvasWrapper clipRect(@NotNull Rect r) {
        this.canvas.clipRect(r, true);
        return this;
    }

    public CanvasWrapper clipRect(float x, float y, float width, float height) {
        //I don't really know why but anti alias should be true, at least when clipping certain float values
        this.canvas.clipRect(Rect.makeXYWH(x, y, width, height), true);
        return this;
    }

    public CanvasWrapper clipRRect(@NotNull RRect r) {
        this.canvas.clipRRect(r, true);
        return this;
    }

    public int saveLayerAlpha(Rect bounds, int alpha) {
        return this.canvas.saveLayerAlpha(bounds, alpha);
    }

    public int saveLayerAlpha(float x, float y, float width, float height, int alpha) {
        return this.saveLayerAlpha(Rect.makeXYWH(x, y, width, height), alpha);
    }

    public CanvasWrapper drawPath(@NotNull Path path, Color color) {
        try (Paint paint = SkiaUtils.createPaintForColor(color)) {
            return this.drawPath(path, paint);
        }
    }

    public CanvasWrapper drawPath(@NotNull Path path, @NotNull Paint paint) {
        this.canvas.drawPath(path, paint);
        return this;
    }

    public CanvasWrapper drawCircle(float x, float y, float radius, Color color) {
        try (Paint paint = SkiaUtils.createPaintForColor(color)) {
            return this.drawCircle(x, y, radius, paint);
        }
    }

    public CanvasWrapper drawCircle(float x, float y, float radius, @NotNull Paint paint) {
        this.canvas.drawCircle(x, y, radius, paint);
        return this;
    }


    public void drawRect(float x, float y, float width, float height, Color color) {
        try (Paint paint = SkiaUtils.createPaintForColor(color)) {
            this.drawRect(x, y, width, height, paint);
        }
    }

    public void drawRect(float x, float y, float width, float height, Paint paint) {
        this.drawRect(Rect.makeXYWH(x, y, width, height), paint);
    }

    public void drawRect(Rect rect, Paint paint) {
        this.canvas.drawRect(rect, paint);

    }

    public void drawRRect(float x, float y, float width, float height, float radius, Color color) {
        try (Paint paint = SkiaUtils.createPaintForColor(color)) {
            this.drawRRect(x, y, width, height, radius, paint);
        }
    }

    public void drawRRect(float x, float y, float width, float height, float radius, Paint paint) {
        this.canvas.drawRRect(RRect.makeXYWH(x, y, width, height, radius), paint);
    }

    public void drawRRect(RRect rect, Paint paint) {
        this.canvas.drawRRect(rect, paint);
    }

    public void drawString(String text, float x, float y, Font font, Color color) {
        try (Paint paint = SkiaUtils.createPaintForColor(color)) {
            this.drawString(text, x, y, font, paint);
        }
    }

    public void drawString(String text, float x, float y, Font font, Paint paint) {
        SkiaUtils.drawString(this.canvas, text, x, y, font, paint);
    }

    public void drawString(String text, float x, float y, Font font, Color color, boolean shadow) {
        try (Paint paint = SkiaUtils.createPaintForColor(color)) {
            this.drawString(text, x, y, font, paint, shadow);
        }
    }


    public void drawString(String text, float x, float y, Font font, Paint paint, boolean shadow) {
        SkiaUtils.drawStringForMinecraft(this, text, x, y, font, paint, shadow);
    }

    public Path getTooltipPath(float x, float y, float width, float height, float borderRadius, float thumbCenterX, float thumbRadius) {
        Path mainRectangle = new Path();
        mainRectangle.addRRect(RRect.makeXYWH(x, y, width, height, borderRadius));
        Path thumb = new Path();
        thumb.moveTo(thumbCenterX + thumbRadius, height);
        thumb.lineTo(thumbCenterX, height + thumbRadius);
        thumb.lineTo(thumbCenterX - thumbRadius, height);
        Path path = Path.makeCombining(mainRectangle, thumb, PathOp.UNION);
        mainRectangle.close();
        thumb.close();
        return path;
    }

    //yikes, this probably shouldn't be in this class but we will keep it for now
    public void drawTooltip(float x, float y, float width, float height, float borderRadius, float thumbCenterX, float thumbRadius) {
        try (Path path = this.getTooltipPath(x, y, width, height, borderRadius, thumbCenterX, thumbRadius)) {
            this.drawPath(path, MainFrame.s_selectorButtonSelectedColor.get());
            try (var stroke = SkiaUtils.createStrokePaint(MainFrame.s_lineColor.get(), MainFrame.s_lineWidth.get())) {
                this.drawPath(path, stroke);
            }
        }
    }

    @Accessors(chain = true)
    public static class TextBuilder {

        @Setter
        private Font font;
        @Setter
        private String text;
        @Setter
        private float x;
        @Setter
        private float y;
        @Setter
        private Paint paint;//will use paint first if not null
        @Setter
        private Color color;
        @Setter
        private boolean shadow;
        @Setter
        @Getter
        private float fontSize;
        @Setter
        private HorizontalAlignment horizontalAlignment;
        @Setter
        private VerticalAlignment verticalAlignment;

        public TextBuilder() {
            this.setDefaults();
        }

        public TextBuilder setDefaults() {
            this.font = Fonts.getDefault();
            this.text = "default";
            this.x = 0;
            this.y = 0;
            this.paint = null;
            this.color = Color.white;
            this.shadow = false;
            this.fontSize = Fonts.DEFAULT_SIZE;
            this.horizontalAlignment = HorizontalAlignment.LEFT;
            this.verticalAlignment = VerticalAlignment.TOP;
            return this;
        }

        public TextBuilder basic(Text text, float x, float y) {
            return this.basic(text.getString(), x, y);
        }

        public TextBuilder basic(Text text, float x, float y, Color color) {
            return this.basic(text.getString(), x, y, color);
        }

        public TextBuilder basic(String text, float x, float y) {
            return this.setText(text).setX(x).setY(y);
        }

        public TextBuilder basic(String text, float x, float y, Color color) {
            return this.basic(text, x, y).setColor(color);
        }

        public TextBuilder setText(String text) {
            this.text = text;
            return this;
        }

        public TextBuilder setText(Text text) {
            return this.setText(text.getString());
        }

        public void draw(CanvasWrapper canvas) {
            this.font.setSize(this.fontSize);

            float textWidth = this.font.measureTextWidth(this.text);
            float xOffset = switch (this.horizontalAlignment) {
                case CENTER -> textWidth / 2f;
                case RIGHT -> textWidth;
                default -> 0;
            };

            float yOffset = switch (this.verticalAlignment) {
                case MIDDLE -> this.fontSize / 2f;
                case BOTTOM -> this.fontSize;
                default -> 0;
            };

            if (this.paint != null) {
                canvas.drawString(this.text, this.x - xOffset, this.y - yOffset, this.font, this.paint, this.shadow);
            } else {
                canvas.drawString(this.text, this.x - xOffset, this.y - yOffset, this.font, this.color, this.shadow);
            }

            this.font.setSize(Fonts.DEFAULT_SIZE);
        }

        public float getTextWidth() {
            this.font.setSize(this.fontSize);
            float width = this.font.measureTextWidth(this.text);
            this.font.setSize(Fonts.DEFAULT_SIZE);
            return width;
        }

        public Rect measureText() {
            this.font.setSize(this.fontSize);
            Rect bounds = this.font.measureText(this.text);
            this.font.setSize(Fonts.DEFAULT_SIZE);
            return bounds;
        }

        public Rect[] getGlyphBounds() {
            this.font.setSize(this.fontSize);
            Rect[] bounds = this.font.getBounds(this.font.getStringGlyphs(this.text));
            this.font.setSize(Fonts.DEFAULT_SIZE);
            return bounds;
        }

        public enum HorizontalAlignment {
            LEFT, CENTER, RIGHT;
        }

        public enum VerticalAlignment {
            TOP, MIDDLE, BOTTOM;
        }
    }

}
