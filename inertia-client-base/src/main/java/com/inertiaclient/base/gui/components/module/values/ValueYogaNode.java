package com.inertiaclient.base.gui.components.module.values;

import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.value.Value;
import lombok.Getter;
import net.minecraft.client.gui.screens.Screen;

public class ValueYogaNode<T extends Value<?>> extends YogaNode {

    @Getter
    private T value;
    private boolean showTooltip = true;

    public ValueYogaNode(T value) {
        this.value = value;
        this.setSearchContext(value.getNameString() + " " + value.getValue());
        this.setTooltip(() -> value.getDescriptionString());
        this.setShouldRenderTooltip(() -> this.showTooltip);

        this.setHoverCallback(startHover -> {
            if (startHover) {
                showTooltip = true;
            }
        });

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (Screen.hasShiftDown() && button == ButtonIdentifier.LEFT) {
                YogaNode advancedInfoContainer = this.createAdvancedInfoContainer(relativeMouseX, relativeMouseY);
                if (advancedInfoContainer != null) {
                    ModernClickGui.MODERN_CLICK_GUI.getRoot().addChild(advancedInfoContainer);
                    showTooltip = false;
                    return true;
                }
            }
            return false;
        });
    }

    protected boolean implIsVisible() {
        return this.value.isVisible();
    }

    public void _valueNodeSetShouldShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
    }

    protected YogaNode createAdvancedInfoContainer(float relativeMouseX, float relativeMouseY) {
        return new GenericAdvancedInfo(this.value, () -> this.getGlobalX() + relativeMouseX, () -> this.getGlobalY());
    }


}
