package com.inertiaclient.base.gui.components.module.values.keybind;

import com.inertiaclient.base.gui.components.DeleteButton;
import com.inertiaclient.base.gui.components.SelectorButton;
import com.inertiaclient.base.gui.components.module.values.ValueNameLabel;
import com.inertiaclient.base.render.yoga.YogaBuilder;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import com.inertiaclient.base.utils.InputUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class KeyBindComponent extends YogaNode {

    private static final Component LABEL = Component.translatable("icb.gui.text.keybind.label");
    private static final Component WAITING = Component.translatable("icb.gui.text.keybind.waiting");

    private boolean waitingForKeyPress;

    public KeyBindComponent(KeyWrapper keyWrapper) {
        this.setSearchContext(LABEL.getString());

        this.styleSetHeight(12);
        this.styleSetAlignItems(AlignItems.CENTER);
        this.styleSetFlexDirection(FlexDirection.ROW);
        this.styleSetJustifyContent(JustifyContent.SPACE_BETWEEN);

        this.addChild(new ValueNameLabel(LABEL));

        var selectorButton = new SelectorButton(() -> waitingForKeyPress ? WAITING.getString() : InputUtils.getKeybindName(keyWrapper.get()), () -> waitingForKeyPress, () -> {
            waitingForKeyPress = !waitingForKeyPress;
        });
        selectorButton.styleSetHeight(10);


        YogaBuilder.getFreshBuilder(this).setGap(GapGutter.COLUMN, 5).addChild(selectorButton).addChild(new DeleteButton(() -> keyWrapper.set(InputUtils.NO_KEY_BIND)));

        this.setKeyPressedCallback((keyCode, scanCode, modifiers) -> {
            if (waitingForKeyPress) {
                InputConstants.Key newKeybind = null;
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) {//no longer listen for a key, and don't bind the key
                    waitingForKeyPress = false;

                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_DELETE) {
                    newKeybind = InputUtils.NO_KEY_BIND;//no keybind
                } else {
                    newKeybind = InputUtils.fromKeyCode(keyCode, scanCode);
                }
                keyWrapper.set(newKeybind);
                waitingForKeyPress = false;

                return true;
            }
            return false;
        });
    }

    public interface KeyWrapper {

        InputConstants.Key get();

        void set(InputConstants.Key key);
    }
}
