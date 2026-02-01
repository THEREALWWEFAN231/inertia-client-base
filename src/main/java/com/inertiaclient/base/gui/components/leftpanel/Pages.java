package com.inertiaclient.base.gui.components.leftpanel;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.Page;
import com.inertiaclient.base.gui.components.SvgComponent;
import com.inertiaclient.base.gui.components.config.ConfigPage;
import com.inertiaclient.base.gui.components.friends.FriendsPage;
import com.inertiaclient.base.gui.components.helpers.ValuesPage;
import com.inertiaclient.base.gui.components.hudeditor.HudEditorPage;
import com.inertiaclient.base.gui.components.module.ModulesPage;
import com.inertiaclient.base.hud.HudEditorScreen;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;

public class Pages extends YogaNode {

    @Getter
    @Setter
    private int selectedPageIndex = -1;

    public Pages() {
        this.styleSetFlexDirection(FlexDirection.COLUMN);
        this.styleSetWidth(100, ExactPercentAuto.PERCENTAGE);
        this.styleSetMargin(YogaEdge.TOP, 10);
        //this.styleSetGap(Yoga.YGGutterRow, 1);

        this.addChild(new PageButton("icb/textures/list.svg", "modules", () -> new Page(createAction("modules"), new ModulesPage()), getChildren().size()));
        this.addChild(new PageButton("icb/textures/list.svg", "config", () -> new Page(createAction("config"), new ConfigPage()), getChildren().size()));
        this.addChild(new PageButton("icb/textures/friend.svg", "friends", () -> new Page(createAction("friends"), new FriendsPage()), getChildren().size()));

        this.addChild(new PageButton("icb/textures/setting-gear.svg", "settings", () -> new Page(createAction("settings"), new ValuesPage(InertiaBase.instance.getSettings().getAllGroups())), getChildren().size()));


        PageButton hudPageButton = new PageButton("icb/textures/hud.svg", "hud", () -> new Page(createAction("hud"), new HudEditorPage()), getChildren().size());
        this.addChild(hudPageButton);

        SvgComponent editButton = new SvgComponent("icb/textures/edit.svg");
        editButton.setColor(() -> MainFrame.s_unselectedTextColor.get());
        editButton.setHoverColor(() -> MainFrame.s_selectedTextColor.get());

        editButton.styleSetWidth(8);
        editButton.styleSetHeight(8);
        editButton.styleSetPosition(YogaEdge.LEFT, 75, ExactPercentAuto.PERCENTAGE);
        editButton.setHoverCursorToIndicateClick();
        editButton.styleSetAlignSelf(AlignItems.CENTER);
        editButton.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                HudEditorScreen.HUD_EDITOR_SCREEN.setParentScreen(InertiaBase.mc.screen);
                InertiaBase.mc.setScreen(HudEditorScreen.HUD_EDITOR_SCREEN);
                return true;
            }
            return false;
        });

        hudPageButton.addAbsoluteChild(editButton);

    }

    public void selectIndex(int index) {
        if (selectedPageIndex != index) {
            MainFrame.pageHolder.addPage(((PageButton) this.getChildAtIndex(index)).createPage());
            selectedPageIndex = index;
        }
    }

    private Component createAction(String id) {
        return Component.translatable("icb.gui.pages." + id + ".action");
    }
}
