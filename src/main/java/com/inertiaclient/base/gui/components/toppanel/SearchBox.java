package com.inertiaclient.base.gui.components.toppanel;

import com.inertiaclient.base.gui.YogaTextField;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import net.minecraft.network.chat.Component;

import java.awt.Color;

public class SearchBox extends YogaTextField {

    public static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 157);

    public SearchBox() {

        this.setTextPadding(() -> 4f);
        this.setPlaceHolderText(Component.translatable("icb.gui.searchbox.placeholder_text"));
        this.setMaxTextLength(90);
        this.setBackgroundColor(() -> BACKGROUND_COLOR);
        this.setBorderRadius(() -> this.getHeight() / 2f);
        this.setChangedListener(newText -> {
            MainFrame.pageHolder.getCurrentPage().getNode().onSearch(newText);
        });

        this.styleSetWidth(100);
        this.styleSetHeight(14);
        this.styleSetMinWidth(45);

        this.setClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                //don't allow top panel to be clicked/dragged when this was clicked
                return true;
            }
            return false;
        });

        //this.setDebug(true);
    }

    public void clearSearch() {
        this.setText("");
    }

}
