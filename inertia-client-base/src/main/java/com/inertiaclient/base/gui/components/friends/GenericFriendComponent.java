package com.inertiaclient.base.gui.components.friends;

import com.inertiaclient.base.gui.components.TextLabel;
import com.inertiaclient.base.gui.components.module.ModuleComponent;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.GenericStyle;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public abstract class GenericFriendComponent extends YogaNode {

    protected String username;
    protected UUID uuid;

    public GenericFriendComponent(String username, UUID uuid) {
        this.username = username;
        this.uuid = uuid;

        this.applyGenericStyle(new GenericStyle().setBorderRadius(() -> 5f).setBackgroundColor(() -> ModuleComponent.BACKGROUND_COLOR));

        this.styleSetMinHeight(18);
        this.setSearchContext(this.username);
        this.styleSetAlignItems(AlignItems.CENTER);
        this.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);
        this.styleSetPadding(YogaEdge.HORIZONTAL, 5);
        //this.styleSetJustifyContent(JustifyContent.FLEX_END);

        this.addChild(createHeadAndName());
        this.addChild(new FriendAddRemoveButton(this.username, this.uuid));
    }

    private YogaNode createHeadAndName() {
        YogaNode headAndName = new YogaNode();
        headAndName.styleSetAlignItems(AlignItems.CENTER);
        headAndName.styleSetGap(GapGutter.COLUMN, 3);
        headAndName.setHoverCursorToIndicateClick();

        headAndName.addChild(this.createHeadDisplay(headAndName));
        headAndName.addChild(new TextLabel(Component.literal(this.username)).setFontSize(() -> 8f).setVerticalAlignment(() -> CanvasWrapper.TextBuilder.VerticalAlignment.TOP).setBlurRadius(() -> headAndName.shouldShowHoveredEffects() ? 1.5f : 0f));

        return headAndName;
    }

    public abstract YogaNode createHeadDisplay(YogaNode headAndName);
}

