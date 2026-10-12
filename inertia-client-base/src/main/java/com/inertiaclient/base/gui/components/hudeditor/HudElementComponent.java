package com.inertiaclient.base.gui.components.hudeditor;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.Page;
import com.inertiaclient.base.gui.components.helpers.ValuesPage;
import com.inertiaclient.base.gui.components.module.ModuleComponent;
import com.inertiaclient.base.hud.HudComponent;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.render.animation.Animations;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import com.inertiaclient.base.utils.CursorUtils;
import com.inertiaclient.base.utils.UIUtils;
import com.inertiaclient.base.value.group.ValueGroup;

import java.awt.Color;
import java.util.ArrayList;

public class HudElementComponent extends YogaNode {

    private AnimationValue toggleAnimation;
    private boolean oldEnabledState;

    public HudElementComponent(HudComponent hudElement) {
        this.oldEnabledState = hudElement.isEnabled();
        this.toggleAnimation = new AnimationValue();
        this.toggleAnimation.setValue(hudElement.isEnabled() ? 1 : 0);

        this.setSearchContext(hudElement.getName().getString());
        this.styleSetMinHeight(30);

        this.styleSetAlignItems(AlignItems.CENTER);
        this.styleSetJustifyContent(JustifyContent.FLEX_END);
        this.styleSetGap(GapGutter.COLUMN, 5);
        this.styleSetBorder(YogaEdge.ALL, 7);

        var clickGuiSettings = InertiaBase.instance.getSettings().getClickGuiSettings();
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            if (hudElement.isEnabled() != oldEnabledState) {
                AnimationValue.tweenEngine.cancelTarget(toggleAnimation);
                toggleAnimation.to(.75f).value(hudElement.isEnabled() ? 1 : 0).ease(Animations.easeOutExpo).start();
                oldEnabledState = hudElement.isEnabled();
            }

            Color enabledColor = UIUtils.colorWithAlpha(clickGuiSettings.getPrimaryColor().getValue().getRenderColor(), ModuleComponent.BACKGROUND_OPACITY);
            canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), 5, UIUtils.transitionColor(ModuleComponent.BACKGROUND_COLOR, enabledColor, Math.clamp(this.toggleAnimation.getValue(), 0, 1)));

            var name = CanvasWrapper.getFreshTextBuilder();
            name.basic(hudElement.getName(), 10, this.getHeight() / 2);
            name.setFontSize(14);
            name.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE).setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.LEFT);
            name.draw(canvas);
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

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                hudElement.setEnabled(!hudElement.isEnabled());
                return true;
            }
            if (button == ButtonIdentifier.RIGHT) {
                ArrayList<ValueGroup> groups = new ArrayList<>(hudElement.getValueGroups().getValueGroups());
                groups.add(hudElement.getGroup().getValuesGroup());
                MainFrame.pageHolder.addPage(new Page(hudElement.getName(), new ValuesPage(groups)));
                return true;
            }
            return false;
        });

        YogaNode enabledSwitch = new YogaNode();
        enabledSwitch.setHoverCursor(CursorUtils.Cursor.HAND);
        enabledSwitch.styleSetWidth(28);
        enabledSwitch.styleSetHeight(12);
        enabledSwitch.setIsVisible(() -> clickGuiSettings.getModuleEnabledSwitch().getValue());
        enabledSwitch.setRenderCallback((graphics, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            float switchWidth = 28;
            float switchHeight = 12;
            ModuleComponent.drawSwitchAnimation(canvas, 0, 0, switchWidth, switchHeight, toggleAnimation.getValue());

            if (hudElement.isEnabled() || toggleAnimation.getValue() != 0) {
                CanvasWrapper.getFreshTextBuilder().basic("on", 3, switchHeight / 2f).setColor(UIUtils.colorWithAlpha(Color.white, Math.clamp(toggleAnimation.getValue(), 0, 1))).setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE).draw(canvas);
            }
            if (!hudElement.isEnabled() || toggleAnimation.getValue() != 1) {
                CanvasWrapper.getFreshTextBuilder().basic("off", switchWidth - 3, switchHeight / 2f).setColor(UIUtils.colorWithAlpha(Color.white, 1 - Math.clamp(toggleAnimation.getValue(), 0, 1))).setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.RIGHT).setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE).draw(canvas);
            }
        });
        enabledSwitch.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                hudElement.setEnabled(!hudElement.isEnabled());
                //return true;
            }
            return true;
        });
        this.addChild(enabledSwitch);

    }


}
