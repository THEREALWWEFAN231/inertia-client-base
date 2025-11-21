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
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public class GenericAdvancedInfo extends ValueAdvanceInfoContainer {

    private static final Text COPY_VALUE_SUCCESS = Text.translatable("icb.gui.copy_value_success");
    private static final Text COPY_VALUE_FAILED = Text.translatable("icb.gui.copy_value_failed");
    private static final Text PASTE_VALUE_SUCCESS = Text.translatable("icb.gui.paste_value_success");
    private static final Text PASTE_VALUE_FAILED = Text.translatable("icb.gui.paste_value_failed");

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
                MinecraftClient.getInstance().keyboard.setClipboard(json);
                addNotification(COPY_VALUE_SUCCESS.getString(), false);
            } catch (Exception e) {
                addNotification(String.format(COPY_VALUE_FAILED.getString(), value.getNameString()), true);
                e.printStackTrace();
            }
        });
    }

    public static SelectorButton createPasteButton(Value value) {
        return new SelectorButton(() -> "Paste", () -> false, () -> {
            try {
                String clipboardText = MinecraftClient.getInstance().keyboard.getClipboard();
                JsonElement fromClipboard = JsonParser.parseString(clipboardText);
                value.fromJson(fromClipboard);
                addNotification(PASTE_VALUE_SUCCESS.getString(), false);
            } catch (Exception e) {
                addNotification(String.format(PASTE_VALUE_FAILED.getString(), value.getNameString()), true);
                e.printStackTrace();
            }
        });
    }

    public static void addNotification(String notification, boolean fail) {
        ModernClickGui.MODERN_CLICK_GUI.getNotifcations().addNotification(Notifcations.Notification.builder().text(notification).displayTime(750 + (fail ? 500 : 0)).build());
    }

    public static void addNotification(Text notification, boolean fail) {
        GenericAdvancedInfo.addNotification(notification.getString(), fail);
    }
}