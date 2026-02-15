package com.inertiaclient.base.gui.components.hudeditor;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.Page;
import com.inertiaclient.base.gui.components.helpers.ValuesPage;
import com.inertiaclient.base.gui.components.module.ModuleComponent;
import com.inertiaclient.base.hud.HudComponent;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.ClickType;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.UIUtils;
import com.inertiaclient.base.value.group.ValueGroup;

import java.util.ArrayList;

public class HudElementComponent extends YogaNode {

    public HudElementComponent(HudComponent hudElement) {
        this.setSearchContext(hudElement.getName().getString());
        this.styleSetMinHeight(30);

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), 5, ModuleComponent.BACKGROUND_COLOR);

            var name = CanvasWrapper.getFreshTextBuilder();
            name.basic(hudElement.getName(), 10, this.getHeight() / 2);
            name.setFontSize(14);
            name.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE).setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.LEFT);
            name.draw(canvas);

            float switchHeight = 12;
            ModuleComponent.drawSwitchAnimation(canvas, this.getWidth() - 60, UIUtils.getCenterOffset(switchHeight, this.getHeight()), 28, switchHeight, hudElement.isEnabled() ? 1 : 0);
        });

        this.setHoverCallback(hovered -> {
            if (hovered) {
                InertiaBase.instance.getHudManager().setHighlightedComponent(hudElement);
            } else {
                InertiaBase.instance.getHudManager().setHighlightedComponent(null);
            }
        });
        this.setRemovedCallback(ancestor -> {
            if (InertiaBase.instance.getHudManager().getHighlightedComponent() == hudElement) {
                InertiaBase.instance.getHudManager().setHighlightedComponent(null);
            }
        });

        this.setClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (clickType == ClickType.RELEASED) {
                if (button == ButtonIdentifier.LEFT) {
                    hudElement.setEnabled(!hudElement.isEnabled());
                    return true;
                }
                if (button == ButtonIdentifier.RIGHT) {
                    ArrayList<ValueGroup> groups = new ArrayList<>(hudElement.getValueGroups());
                    groups.add(hudElement.getGroup().getValuesGroup());
                    MainFrame.pageHolder.addPage(new Page(hudElement.getName(), new ValuesPage(groups)));
                    return true;
                }
            }
            return false;
        });

    }


}
