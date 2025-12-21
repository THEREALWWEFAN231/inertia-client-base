package com.inertiaclient.base.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BetterScreen extends Screen {

    public BetterScreen(Component title) {
        super(title);
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        float floatMouseX = (float) (this.minecraft.mouseHandler.xpos() * this.minecraft.getWindow().getGuiScaledWidth() / this.minecraft.getWindow().getScreenWidth());
        float floatMouseY = (float) (this.minecraft.mouseHandler.ypos() * this.minecraft.getWindow().getGuiScaledHeight() / this.minecraft.getWindow().getScreenHeight());

        this.betterRender(context, floatMouseX, floatMouseY, delta);
    }

    public void betterRender(GuiGraphics context, float mouseX, float mouseY, float delta) {

    }
}
