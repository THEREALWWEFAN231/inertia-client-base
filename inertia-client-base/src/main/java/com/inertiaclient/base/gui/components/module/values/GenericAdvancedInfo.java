package com.inertiaclient.base.gui.components.module.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.Notifcations;
import com.inertiaclient.base.gui.components.SelectorButton;
import com.inertiaclient.base.gui.components.module.values.number.ValueAdvanceInfoContainer;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;
import com.inertiaclient.base.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class GenericAdvancedInfo extends ValueAdvanceInfoContainer {

    private static final Component COPY_VALUE_SUCCESS = Component.translatable("icb.gui.copy_value_success");
    private static final String COPY_VALUE_FAILED_STRING = "icb.gui.copy_value_failed";
    private static final Component PASTE_VALUE_SUCCESS = Component.translatable("icb.gui.paste_value_success");
    private static final String PASTE_VALUE_FAILED_STRING = "icb.gui.paste_value_failed";

    public GenericAdvancedInfo(Value value, Supplier<Float> xPosition, Supplier<Float> yPosition) {
        super(value, xPosition, yPosition);
    }

    @Override
    public YogaNode createContentContainer() {
        YogaNode content = new YogaNode();
        content.styleSetFlexDirection(FlexDirection.COLUMN);
        content.styleSetAlignItems(AlignItems.CENTER);
        content.styleSetGap(GapGutter.ROW, 5);

        {
            YogaNode buttonsContainer = new YogaNode();
            content.addChild(buttonsContainer);

            buttonsContainer.styleSetGap(GapGutter.COLUMN, 5);

            buttonsContainer.addChild(GenericAdvancedInfo.createDefaultButton(this.getValue()));
            buttonsContainer.addChild(GenericAdvancedInfo.createCopyButton(this.getValue()));
            buttonsContainer.addChild(GenericAdvancedInfo.createPasteButton(this.getValue()));
        }

        return content;
    }

    public static SelectorButton createDefaultButton(Value value) {
        return new SelectorButton(() -> "Default", () -> false, () -> {
            value.setValue(value.getDefaultValue());
        });
    }

    public static SelectorButton createCopyButton(Value value) {
        return new SelectorButton(() -> "Copy", () -> false, () -> {
            try {
                String json = InertiaBase.instance.getFileManager().getNormalGson().toJson(value.toJson());
                Minecraft.getInstance().keyboardHandler.setClipboard(json);
                addNotification(COPY_VALUE_SUCCESS, false);
            } catch (Exception e) {
                addNotification(Component.translatable(COPY_VALUE_FAILED_STRING, value.getNameString()), true);
                e.printStackTrace();
            }
        });
    }

    public static SelectorButton createPasteButton(Value value) {
        return new SelectorButton(() -> "Paste", () -> false, () -> {
            try {
                String clipboardText = Minecraft.getInstance().keyboardHandler.getClipboard();
                JsonElement fromClipboard = JsonParser.parseString(clipboardText);
                value.fromJson(fromClipboard);
                addNotification(PASTE_VALUE_SUCCESS, false);
            } catch (Exception e) {
                addNotification(Component.translatable(PASTE_VALUE_FAILED_STRING, value.getNameString()), true);
                e.printStackTrace();
            }
        });
    }

    public static void addNotification(Component notification, boolean fail) {
        ModernClickGui.MODERN_CLICK_GUI.getNotifcations().addNotification(Notifcations.Notification.builder().text(notification).displayTime(750 + (fail ? 500 : 0)).build());
    }
}