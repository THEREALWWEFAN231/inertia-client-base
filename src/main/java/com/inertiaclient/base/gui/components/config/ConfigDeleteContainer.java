package com.inertiaclient.base.gui.components.config;

import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.SelectorButton;
import com.inertiaclient.base.gui.components.helpers.InfoContainer;
import com.inertiaclient.base.gui.components.module.values.GenericAdvancedInfo;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public class ConfigDeleteContainer extends InfoContainer<File> {

    public ConfigDeleteContainer(File configFolder, String configName, Supplier<Float> xPosition, Supplier<Float> yPosition) {
        super(configFolder, () -> "Delete " + configName + "?", xPosition, yPosition);
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

            buttonsContainer.addChild(new SelectorButton(() -> "Delete", () -> false, () -> {
                try {
                    FileUtils.deleteDirectory(this.getWrapper());
                    GenericAdvancedInfo.addNotification("Successfully deleted config", false);
                    ((ConfigPage) MainFrame.pageHolder.getCurrentPage().getNode()).refreshLocalTab();
                    this.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    GenericAdvancedInfo.addNotification("Failed to delete config", true);
                }
            }));
            buttonsContainer.addChild(new SelectorButton(() -> "Close", () -> false, this::close));
        }

        return content;
    }
}
