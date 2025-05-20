package com.inertiaclient.base.gui.components.tabbedpage;

import com.inertiaclient.base.gui.components.SelectorButton;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;
import com.inertiaclient.base.render.yoga.layouts.YogaEdge;
import lombok.Getter;

import java.util.ArrayList;

public abstract class TabbedPage extends YogaNode {

    @Getter
    private int selectedButtonIndex = -1;
    private ArrayList<Tab> tabs;

    private YogaNode content;

    public TabbedPage() {
        this.setFirstInitCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            this.setup();
        });
    }

    private void setup() {
        this.tabs = this.createTabs();
        this.styleSetFlexGrow(1);
        this.styleSetFlexShrink(1);
        this.styleSetFlexDirection(FlexDirection.COLUMN);

        YogaNode buttonsContainer = new YogaNode();
        {
            buttonsContainer.styleSetFlexShrink(0);
            buttonsContainer.styleSetMargin(YogaEdge.BOTTOM, 3);
            buttonsContainer.setShouldScissorChildren(true);
            buttonsContainer.enableHorizontalScrollbar();

            YogaNode allTabs = new YogaNode();
            buttonsContainer.addChild(allTabs);
            allTabs.styleSetGap(GapGutter.COLUMN, 5);
            allTabs.styleSetFlexShrink(0);
            allTabs.styleSetFlexGrow(0);

            for (int i = 0; i < tabs.size(); i++) {
                int index = i;
                allTabs.addChild(new SelectorButton(() -> tabs.get(index).getLabel(), () -> selectedButtonIndex == index, () -> {
                    setSelectedIndex(index);
                }));
            }
        }
        this.addChild(buttonsContainer);


        content = new YogaNode();
        //umm, needed for EntityTypePage(to position select all and deselect buttons at bottom) I don't know if we want this for every tabbed page though
        content.styleSetFlexGrow(1);
        content.styleSetFlexShrink(1);
        this.addChild(content);

        setSelectedIndex(defaultSelectedIndex());
    }

    public void setSelectedIndex(int index) {
        if (selectedButtonIndex == index) {
            return;
        }
        this.selectedButtonIndex = index;
        content.removeAllChildren();

        Tab tab = tabs.get(index);
        content.addChild(tab.getYogaNode());
        if (tab.onSelected() != null) {
            tab.onSelected().accept(tab.getYogaNode());
        }

        this.content.refreshSearch();
    }

    public int defaultSelectedIndex() {
        return 0;
    }

    public abstract ArrayList<Tab> createTabs();

}
