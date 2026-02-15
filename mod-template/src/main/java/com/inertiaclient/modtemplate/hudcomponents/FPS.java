package com.inertiaclient.modtemplate.hudcomponents;

import com.inertiaclient.base.hud.helpers.BasicTextComponent;
import net.minecraft.client.Minecraft;

public class FPS extends BasicTextComponent {

    public FPS() {
        super("fps");
    }

    @Override
    public String getRenderedText(boolean preview) {
        return String.format("FPS %s", Minecraft.getInstance().getFps());
    }
}