package com.inertiaclient.base.gui.components.config;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.components.DeleteButton;
import com.inertiaclient.base.gui.components.SelectorButton;
import com.inertiaclient.base.gui.components.module.ModuleComponent;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.YogaBuilder;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.ExactPercentAuto;
import com.inertiaclient.base.render.yoga.layouts.JustifyContent;

import java.io.File;

public class ConfigComponent extends YogaNode {

    public ConfigComponent(File configFolder) {
        this.styleSetMinHeight(30);
        this.styleSetJustifyContent(JustifyContent.FLEX_END);

        YogaNode buttonsContainer = YogaBuilder.getFreshBuilder(this).setWidth(30, ExactPercentAuto.PERCENTAGE).setAlignItems(AlignItems.CENTER).setJustifyContent(JustifyContent.SPACE_AROUND).build();

        buttonsContainer.addChild(new SelectorButton(() -> "Load", () -> false, () -> {
            InertiaBase.instance.getFileManager().loadConfig(configFolder);
        }));

        DeleteButton deleteButton = new DeleteButton(() -> {

        });
        deleteButton.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            ModernClickGui.MODERN_CLICK_GUI.getRoot().addChild(new ConfigDeleteContainer(configFolder, configFolder.getName(), () -> deleteButton.getGlobalX() + relativeMouseX, () -> deleteButton.getGlobalY()));
            return true;
        });
        buttonsContainer.addChild(deleteButton);

        String configName = configFolder.getName();
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), 5, ModuleComponent.BACKGROUND_COLOR);


            var nameTextBuilder = CanvasWrapper.getFreshTextBuilder();
            nameTextBuilder.basic(configName, 7, this.getHeight() / 2);
            nameTextBuilder.setFontSize(14);
            nameTextBuilder.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            nameTextBuilder.draw(canvas);
        });
    }

}
