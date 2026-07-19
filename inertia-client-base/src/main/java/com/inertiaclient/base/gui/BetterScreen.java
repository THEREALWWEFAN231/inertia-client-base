package com.inertiaclient.base.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BetterScreen extends Screen {

    public BetterScreen(Component title) {
        super(title);
    }

    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        float floatMouseX = (float) (this.minecraft.mouseHandler.xpos() * this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth());
        float floatMouseY = (float) (this.minecraft.mouseHandler.ypos() * this.minecraft.getWindow().getGuiScaledHeight() / this.minecraft.getWindow().getScreenHeight());

        this.betterRender(graphics, floatMouseX, floatMouseY, delta);
    }

    public void betterRender(GuiGraphicsExtractor graphics, float mouseX, float mouseY, float delta) {

    }
}
