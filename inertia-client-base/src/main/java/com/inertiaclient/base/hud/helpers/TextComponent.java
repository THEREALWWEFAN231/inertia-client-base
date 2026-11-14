package com.inertiaclient.base.hud.helpers;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.hud.HudComponent;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.Fonts;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

public abstract class TextComponent extends HudComponent {


    private String renderedText;

    public TextComponent(String name) {
        super(name);
    }

    @Override
    public void beforeRender(boolean preview, boolean editor, CanvasWrapper canvas) {
        try {
            this.renderedText = this.getRenderedText(preview, editor);
        } catch (Exception e) {
            this.renderedText = "Error getting text";
            InertiaBase.LOGGER.error("Error getting text for TextComponent {}", this.getClass().getName(), e);
        }

        this.setWidth(this.renderedText != null ? CanvasWrapper.getFreshTextBuilder().setText(this.renderedText).getTextWidth() : 0);
        this.setHeight(this.renderedText != null ? Fonts.DEFAULT_SIZE : 0);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, boolean editor, CanvasWrapper canvas) {
        if (renderedText == null) {
            return;
        }

        CanvasWrapper.getFreshTextBuilder().basic(this.renderedText, 0, 0, this.getColor()).setShadow(this.drawShadow()).draw(canvas);
    }

    @Nullable
    public abstract String getRenderedText(boolean preview, boolean editor);

    public Color getColor() {
        return Color.white;
    }

    public boolean drawShadow() {
        return true;
    }

}
