package com.inertiaclient.base.gui;

import com.inertiaclient.base.render.yoga.ClickType;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.utils.CursorUtils;
import lombok.Setter;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class YogaTextField extends YogaNode {

    private TextField textField;
    @Setter
    private Runnable beforeTextFieldRender;
    @Setter
    private Runnable afterTextFieldClick;

    public YogaTextField() {
        this.setHoverCursor(CursorUtils.Cursor.IBEAM);
        this.textField = new TextField();

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            this.textField.setX(this.getGlobalX());
            this.textField.setY(this.getGlobalY());
            this.textField.setWidth(this.getWidth());
            this.textField.setHeight(this.getHeight());
            if (beforeTextFieldRender != null) {
                beforeTextFieldRender.run();
            }

            canvas.save();
            canvas.translate(-this.textField.getX(), -this.textField.getY());
            this.textField.render(canvas, globalMouseX, globalMouseY, delta);
            canvas.restore();
        });

        this.setGlobalClickCallback((mouseX, mouseY, button, clickType) -> {
            if (clickType == ClickType.CLICKED) {
                this.textField.mouseClicked(mouseX, mouseY, button);
                if (afterTextFieldClick != null) {
                    this.afterTextFieldClick.run();
                }
            } else {
                this.textField.mouseReleased(mouseX, mouseY, button);
            }
            return false;
        });

        this.setKeyPressedCallback((keyCode, scanCode, modifiers) -> this.textField.keyPressed(keyCode, scanCode, modifiers));
        this.setCharTypedCallback((chr, modifiers) -> this.textField.charTyped(chr, modifiers));
    }

    public YogaTextField setTextPadding(Supplier<Float> padding) {
        this.textField.setTextPadding(padding);
        return this;
    }

    public YogaTextField setTextColor(Supplier<Color> textColor) {
        this.textField.setTextColor(textColor);
        return this;
    }

    public YogaTextField setBackgroundColor(Supplier<Color> backgroundColor) {
        this.textField.setBackgroundColor(backgroundColor);
        return this;
    }

    public YogaTextField setBorderRadius(Supplier<Float> borderRadius) {
        this.textField.setBorderRadius(borderRadius);
        return this;
    }

    public YogaTextField setStrokeColor(Supplier<Color> strokeColor) {
        this.textField.setStrokeColor(strokeColor);
        return this;
    }

    public YogaTextField setStrokeWidth(Supplier<Float> strokeWidth) {
        this.textField.setStrokeWidth(strokeWidth);
        return this;
    }

    public YogaTextField setFontSize(float fontSize) {
        this.textField.setFontSize(fontSize);
        return this;
    }

    public YogaTextField setSelectionColor(Supplier<Color> selectionColor) {
        this.textField.setSelectionColor(selectionColor);
        return this;
    }

    public YogaTextField setPlaceHolderText(Component placeHolderText) {
        this.textField.setPlaceHolderText(placeHolderText);
        return this;
    }

    public YogaTextField setPlaceHolderTextColor(Color placeHolderTextColor) {
        this.textField.setPlaceHolderTextColor(placeHolderTextColor);
        return this;
    }

    public YogaTextField setMaxTextLength(int maxTextLength) {
        this.textField.setMaxTextLength(maxTextLength);
        return this;
    }


    public YogaTextField setText(String text) {
        this.textField.setText(text);
        return this;
    }

    public YogaTextField setChangedListener(Consumer<String> changedListener) {
        this.textField.setChangedListener(changedListener);
        return this;
    }

    public YogaTextField setEnterAction(Consumer<String> enterAction) {
        this.textField.setEnterAction(enterAction);
        return this;
    }

    public String getText() {
        return this.textField.getText();
    }

    public TextField getTextField() {
        return this.textField;
    }

}
