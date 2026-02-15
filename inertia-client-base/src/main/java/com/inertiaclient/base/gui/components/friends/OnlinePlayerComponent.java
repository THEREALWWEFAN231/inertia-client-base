package com.inertiaclient.base.gui.components.friends;

import com.inertiaclient.base.gui.components.NativeRenderComponent;
import com.inertiaclient.base.render.skia.SkiaNativeRender;
import com.inertiaclient.base.render.yoga.YogaNode;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;

public class OnlinePlayerComponent extends GenericFriendComponent {

    private PlayerInfo playerInfo;

    public OnlinePlayerComponent(PlayerInfo playerInfo) {
        super(playerInfo.getProfile().getName(), playerInfo.getProfile().getId());

        this.playerInfo = playerInfo;
    }

    @Override
    public YogaNode createHeadDisplay(YogaNode headAndName) {
        SkiaNativeRender headRenderer = new SkiaNativeRender();
        headRenderer.setNativeWidth(() -> 10f);
        headRenderer.setNativeHeight(() -> 10f);
        headRenderer.setBlurRadius(() -> headAndName.shouldShowHoveredEffects() ? 3f : 0f);
        headRenderer.setSetNativeRender(guiGraphics -> {
            boolean showLayer = true;
            boolean upsideDown = false;
            PlayerFaceRenderer.draw(guiGraphics, playerInfo.getSkin().texture(), 0, 0, 10, showLayer, upsideDown, -1);
        });

        return new NativeRenderComponent(headRenderer);
    }
}
