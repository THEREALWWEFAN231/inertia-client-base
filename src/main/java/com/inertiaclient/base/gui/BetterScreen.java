package com.inertiaclient.base.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class BetterScreen extends Screen {

    public BetterScreen(Text title) {
        super(title);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float floatMouseX = (float) (this.client.mouse.getX() * this.client.getWindow().getScaledWidth() / this.client.getWindow().getWidth());
        float floatMouseY = (float) (this.client.mouse.getY() * this.client.getWindow().getScaledHeight() / this.client.getWindow().getHeight());

        this.betterRender(context, floatMouseX, floatMouseY, delta);
    }

    public void betterRender(DrawContext context, float mouseX, float mouseY, float delta) {

    }
}
