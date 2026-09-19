package com.inertiaclient.base.hud;

import com.inertiaclient.base.gui.BetterScreen;
import com.inertiaclient.base.render.skia.instances.SkiaInstance;
import com.inertiaclient.base.render.skia.instances.SkiaVulkanInstance;
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
    private SkiaInstance skiaInstance;
    private HudEditor hudEditor;
    @Setter
    private Screen parentScreen;

    public HudEditorScreen() {
        super(Component.literal(""));

        if (this.skiaInstance == null) {
            this.skiaInstance = new SkiaVulkanInstance((graphics, mouseX, mouseY, delta) -> {
                this.hudEditor.beforeRender(this.skiaInstance);
                this.hudEditor.render(graphics, mouseX, mouseY, delta, this.skiaInstance.getCanvasWrapper());
            });
        }

        this.hudEditor = new HudEditor(this);
    }

    @Override
    public void betterRender(GuiGraphicsExtractor graphics, float mouseX, float mouseY, float delta) {
        this.skiaInstance.drawAndRender(graphics, mouseX, mouseY, delta);
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
