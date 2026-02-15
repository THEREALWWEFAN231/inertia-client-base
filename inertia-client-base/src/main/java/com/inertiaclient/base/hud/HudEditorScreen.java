package com.inertiaclient.base.hud;

import com.inertiaclient.base.gui.BetterScreen;
import com.inertiaclient.base.render.skia.SkiaOpenGLInstance;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HudEditorScreen extends BetterScreen {

    public static final HudEditorScreen HUD_EDITOR_SCREEN = new HudEditorScreen();

    @Getter
    private SkiaOpenGLInstance skiaInstance;
    private HudEditor hudEditor;
    @Setter
    private Screen parentScreen;

    public HudEditorScreen() {
        super(Component.literal(""));

        if (skiaInstance == null) {
            skiaInstance = new SkiaOpenGLInstance();
        }

        this.hudEditor = new HudEditor(this);
    }

    @Override
    public void betterRender(GuiGraphics context, float mouseX, float mouseY, float delta) {
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
    public void onClose() {
        this.minecraft.setScreen(this.parentScreen);
    }

}
