package com.inertiaclient.base.gui.components.config;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.YogaTextField;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.SelectButton;
import com.inertiaclient.base.gui.components.TextLabel;
import com.inertiaclient.base.gui.components.helpers.VerticalListContainer;
import com.inertiaclient.base.gui.components.module.values.GenericAdvancedInfo;
import com.inertiaclient.base.gui.components.tabbedpage.Tab;
import com.inertiaclient.base.gui.components.tabbedpage.TabbedPage;
import com.inertiaclient.base.gui.components.toppanel.SearchBox;
import com.inertiaclient.base.hud.HudComponent;
import com.inertiaclient.base.module.Module;
import com.inertiaclient.base.render.yoga.YogaBuilder;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import com.inertiaclient.base.value.Value;
import com.inertiaclient.base.value.group.ValueGroup;

import java.io.File;
import java.util.ArrayList;

public class ConfigPage extends TabbedPage {

    private Tab<VerticalListContainer> localTab;

    public int defaultSelectedIndex() {
        return 1;
    }

    public ArrayList<Tab> createTabs() {
        var tabs = new ArrayList<Tab>();

        Tab<VerticalListContainer> all = new Tab("Public", new VerticalListContainer());
        tabs.add(all);

        this.localTab = new Tab("Local", new VerticalListContainer());
        tabs.add(this.localTab);

        Tab<YogaNode> newTab = new Tab("New", createNewNode());
        tabs.add(newTab);

        Tab<YogaNode> defaultTab = new Tab("Default", createDefaultNode());
        tabs.add(defaultTab);

        this.refreshLocalTab();

        return tabs;
    }

    public void refreshLocalTab() {
        this.localTab.getYogaNode().getListNode().removeAllChildren();

        File[] configs = InertiaBase.instance.getFileManager().getConfigs();
        for (int i = 0; i < configs.length; i++) {
            File configFolder = configs[i];

            this.localTab.getYogaNode().addToList(new ConfigComponent(configFolder));
        }
    }

    private YogaNode createNewNode() {
        boolean[] saveProperties = {true, true, true};


        YogaNode yogaNode = YogaBuilder.getFreshBuilder().setFlexGrow(1).setFlexShrink(0).setAlignItems(AlignItems.CENTER).setJustifyContent(JustifyContent.CENTER).setFlexDirection(FlexDirection.COLUMN).build();
        YogaNode middleContainer = YogaBuilder.getFreshBuilder(yogaNode).setWidth(33.333f, ExactPercentAuto.PERCENTAGE).setGap(GapGutter.ROW, 10).setFlexDirection(FlexDirection.COLUMN).build();

        YogaNode nameAndSettings = YogaBuilder.getFreshBuilder(middleContainer).setFlexDirection(FlexDirection.COLUMN).setGap(GapGutter.ROW, 1).build();

        YogaTextField name = new YogaTextField();

        {
            name.setPlaceHolderText("Name");

            name.setBackgroundColor(() -> SearchBox.BACKGROUND_COLOR);
            //name.styleSetWidth(100, ExactPercentAuto.PERCENTAGE);
            name.styleSetHeight(14);
            name.setTextPadding(() -> 4f);
            name.setStrokeColor(MainFrame.s_lineColor);
            name.setStrokeWidth(() -> .5f);
            //name.styleSetMinWidth(100, ExactPercentAuto.PERCENTAGE);
        }


        YogaNode configSettings = YogaBuilder.getFreshBuilder().setFlexDirection(FlexDirection.COLUMN).setAlignItems(AlignItems.CENTER).setGap(GapGutter.ROW, 1).build();

        configSettings.addChild(new ConfigSaveTextComponent(() -> "Module", saveProperties, 0));
        configSettings.addChild(new ConfigSaveTextComponent(() -> "Hud", saveProperties, 1));
        configSettings.addChild(new ConfigSaveTextComponent(() -> "Setting", saveProperties, 2));

        SelectButton saveButton = new SelectButton(() -> "Save", () -> {
            String configName = name.getText();
            if (configName.isEmpty()) {

            } else {
                boolean result = InertiaBase.instance.getFileManager().saveConfig(configName, saveProperties[0], saveProperties[1], saveProperties[2]);
                if (result) {
                    this.refreshLocalTab();
                    this.setSelectedIndex(1);
                    name.setText("");
                    GenericAdvancedInfo.addNotification("Successfully saved config " + configName, false);
                } else {
                    GenericAdvancedInfo.addNotification("Failed to save config " + configName, true);
                }
            }
        });
        saveButton.styleSetAlignSelf(AlignItems.CENTER);

        nameAndSettings.addChild(name);
        nameAndSettings.addChild(configSettings);
        middleContainer.addChild(saveButton);


        return yogaNode;
    }

    private YogaNode createDefaultNode() {
        boolean[] saveProperties = {true, true, true};


        YogaNode yogaNode = YogaBuilder.getFreshBuilder().setFlexGrow(1).setFlexShrink(0).setAlignItems(AlignItems.CENTER).setJustifyContent(JustifyContent.CENTER).setFlexDirection(FlexDirection.COLUMN).build();
        YogaNode middleContainer = YogaBuilder.getFreshBuilder(yogaNode).setWidth(33.333f, ExactPercentAuto.PERCENTAGE).setGap(GapGutter.ROW, 10).setFlexDirection(FlexDirection.COLUMN).setAlignItems(AlignItems.CENTER).build();

        YogaNode nameAndSettings = YogaBuilder.getFreshBuilder(middleContainer).setFlexDirection(FlexDirection.COLUMN).setGap(GapGutter.ROW, 1).setAlignItems(AlignItems.CENTER).build();

        nameAndSettings.addChild(new TextLabel(() -> "Reset"));

        YogaNode configSettings = YogaBuilder.getFreshBuilder().setFlexDirection(FlexDirection.COLUMN).setAlignItems(AlignItems.CENTER).setGap(GapGutter.ROW, 1).build();
        configSettings.addChild(new ConfigSaveTextComponent(() -> "Module", saveProperties, 0));
        configSettings.addChild(new ConfigSaveTextComponent(() -> "Hud", saveProperties, 1));
        configSettings.addChild(new ConfigSaveTextComponent(() -> "Setting", saveProperties, 2));

        SelectButton loadButton = new SelectButton(() -> "Load", () -> {

            for (Module module : InertiaBase.instance.getModuleManager().getModules()) {
                for (ValueGroup valueGroup : module.getValueGroups()) {
                    for (Value value : valueGroup.getValues()) {
                        value.setValue(value.getDefaultValue());
                    }
                }
            }

            for (HudComponent hudElement : InertiaBase.instance.getHudManager().getComponents()) {
                for (ValueGroup valueGroup : hudElement.getValueGroups()) {
                    for (Value value : valueGroup.getValues()) {
                        value.setValue(value.getDefaultValue());
                    }
                }
            }

        });

        nameAndSettings.addChild(configSettings);
        middleContainer.addChild(loadButton);


        return yogaNode;
    }

}
