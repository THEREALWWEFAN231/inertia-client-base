package com.inertiaclient.base.gui.components.friends;

import com.inertiaclient.base.gui.components.NativeRenderComponent;
import com.inertiaclient.base.render.skia.instances.SkiaNativeRender;
import com.inertiaclient.base.render.yoga.YogaNode;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;

public class OnlinePlayerComponent extends GenericFriendComponent {

    private PlayerInfo playerInfo;

    public OnlinePlayerComponent(PlayerInfo playerInfo) {
        super(playerInfo.getProfile().name(), playerInfo.getProfile().id());

        this.playerInfo = playerInfo;
    }

    @Override
    public YogaNode createHeadDisplay(YogaNode headAndName) {
        SkiaNativeRender headRenderer = new SkiaNativeRender();
        var component = new NativeRenderComponent(headRenderer);

        headRenderer.setBlurRadius(() -> headAndName.shouldShowHoveredEffects() ? 3f : 0f);
        headRenderer.setSetNativeRender(graphics -> {
            boolean showLayer = true;
            boolean upsideDown = false;
            PlayerFaceExtractor.extractRenderState(graphics, playerInfo.getSkin().body().texturePath(), 0, 0, (int) component.getWidth(), showLayer, upsideDown, -1);
        });

        return component.styleSetWidth(10f).styleSetHeight(10f);
    }
}
