package com.inertiaclient.base.hud;

import com.inertiaclient.base.gui.BetterScreen;
import com.inertiaclient.base.render.skia.SkiaVulkanInstance;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

//TODO: extend yogascreen
public class HudEditorScreen extends BetterScreen {

    public static final HudEditorScreen HUD_EDITOR_SCREEN = new HudEditorScreen();

    @Getter
    private SkiaVulkanInstance skiaInstance;
    private HudEditor hudEditor;
    @Setter
    private Screen parentScreen;

    public HudEditorScreen() {
        super(Component.literal(""));

        if (skiaInstance == null) {
            skiaInstance = new SkiaVulkanInstance();
        }

        this.hudEditor = new HudEditor(this);
    }

    @Override
    public void betterRender(GuiGraphicsExtractor graphics, float mouseX, float mouseY, float delta) {
        hudEditor.beforeRender(this.skiaInstance);

        skiaInstance.setup(graphics, () -> {
            hudEditor.render(graphics, mouseX, mouseY, delta, skiaInstance.getCanvasWrapper());
        });
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        float mouseX = (float) mouseButtonEvent.x();
        float mouseY = (float) mouseButtonEvent.y();

        this.hudEditor.mouseClicked(mouseX, mouseY, ButtonIdentifier.fromGLFW(mouseButtonEvent.button()));
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        float mouseX = (float) mouseButtonEvent.x();
        float mouseY = (float) mouseButtonEvent.y();

        this.hudEditor.mouseReleased(mouseX, mouseY, ButtonIdentifier.fromGLFW(mouseButtonEvent.button()));
        return false;
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parentScreen);
    }

}
