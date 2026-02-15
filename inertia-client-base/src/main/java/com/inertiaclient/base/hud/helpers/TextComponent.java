package com.inertiaclient.base.hud.helpers;

import com.inertiaclient.base.hud.HudComponent;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.Fonts;
import com.inertiaclient.base.render.skia.SkiaUtils;
import io.github.humbleui.skija.Paint;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

public abstract class TextComponent extends HudComponent {


    private String renderedText;

    public TextComponent(String name) {
        super(name);

        this.setWidth(() -> this.renderedText != null ? Fonts.getDefault().measureTextWidth(this.renderedText) : 0);
        this.setHeight(() -> 9f);
    }

    @Override
    public void beforeRender(boolean editor, CanvasWrapper canvas) {
        this.renderedText = this.getRenderedText(editor);

    }

    @Override
    public void render(GuiGraphics drawContext, boolean editor, CanvasWrapper canvas) {
        if (renderedText == null) {
            return;
        }

        try (Paint paint = SkiaUtils.createPaintForColor(getColor())) {
            SkiaUtils.drawStringForMinecraft(canvas, this.renderedText, 0, 0, paint);
        }
    }

    @Nullable
    public abstract String getRenderedText(boolean preview);

    public Color getColor() {
        return Color.white;
    }

    public boolean drawShadow() {
        return false;
    }

}
