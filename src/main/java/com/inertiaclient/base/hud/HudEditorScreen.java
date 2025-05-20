package com.inertiaclient.base.hud;

import com.inertiaclient.base.gui.BetterScreen;
import com.inertiaclient.base.render.skia.SkiaOpenGLInstance;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HudEditorScreen extends BetterScreen {

    public static final HudEditorScreen HUD_EDITOR_SCREEN = new HudEditorScreen();

    @Getter
    private SkiaOpenGLInstance skiaInstance;
    private HudEditor hudEditor;
    @Setter
    private Screen parentScreen;

    public HudEditorScreen() {
        super(Text.literal(""));

        if (skiaInstance == null) {
            skiaInstance = new SkiaOpenGLInstance();
        }

        this.hudEditor = new HudEditor(this);
    }

    @Override
    public void betterRender(DrawContext context, float mouseX, float mouseY, float delta) {
        hudEditor.beforeRender(this.skiaInstance);

        skiaInstance.setup(() -> {
            hudEditor.render(context, mouseX, mouseY, delta, skiaInstance.getCanvasWrapper());
        });
    }

    @Override
    public boolean mouseClicked(double mouseXD, double mouseYD, int button) {
        float mouseX = (float) mouseXD;
        float mouseY = (float) mouseYD;

        this.hudEditor.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseXD, double mouseYD, int button) {
        float mouseX = (float) mouseXD;
        float mouseY = (float) mouseYD;

        this.hudEditor.mouseReleased(mouseX, mouseY, button);
        return false;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parentScreen);
    }

}
