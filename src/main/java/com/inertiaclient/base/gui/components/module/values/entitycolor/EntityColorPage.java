package com.inertiaclient.base.gui.components.module.values.entitycolor;

import com.inertiaclient.base.gui.components.SelectorButton;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import com.inertiaclient.base.value.impl.EntityTypeColorValue;
import lombok.Getter;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.text.Text;

public class EntityColorPage extends YogaNode {

    @Getter
    private EntityTypeColorValue entityTypeColorValue;
    private SpawnGroupContainer spawnGroupContainer;
    private YogaNode groupComponentContainer;

    private int selectedIndex;

    public EntityColorPage(EntityTypeColorValue entityTypeColorValue) {
        this.entityTypeColorValue = entityTypeColorValue;

        this.styleSetFlexGrow(1);
        this.styleSetFlexShrink(1);
        this.styleSetFlexDirection(FlexDirection.COLUMN);
        //this.styleSetAlignItems(Yoga.YGAlignCenter);

        {
            YogaNode spawnGroupsContainer = new YogaNode();
            this.addChild(spawnGroupsContainer);
            spawnGroupsContainer.styleSetFlexShrink(0);
            spawnGroupsContainer.styleSetMargin(YogaEdge.BOTTOM, 3);

            YogaNode allButtons = new YogaNode();
            allButtons.styleSetGap(GapGutter.COLUMN, 5);
            allButtons.styleSetFlexShrink(0);
            allButtons.styleSetFlexGrow(0);
            spawnGroupsContainer.addChild(allButtons);
            spawnGroupsContainer.enableHorizontalScrollbar();
            spawnGroupsContainer.setShouldScissorChildren(true);
            allButtons.addChild(new SelectorButton(Text.translatable("icb.gui.pages.entitycolor.buttons.all"), () -> selectedIndex == 0, () -> {
                changeContents(null);
            }));
            for (SpawnGroup spawnGroup : SpawnGroup.values()) {
                allButtons.addChild(new SelectorButton(() -> spawnGroup.getName(), () -> selectedIndex == spawnGroup.ordinal() + 1, () -> {
                    changeContents(spawnGroup);
                }));
            }
        }


        {
            YogaNode emptySpace = new YogaNode();
            this.addChild(emptySpace);
            emptySpace.styleSetFlexGrow(1);
            emptySpace.styleSetFlexShrink(1);
            emptySpace.styleSetFlexDirection(FlexDirection.COLUMN);
            emptySpace.styleSetAlignItems(AlignItems.CENTER);

            this.groupComponentContainer = new YogaNode();
            this.groupComponentContainer.styleSetFlexShrink(0);
            emptySpace.addChild(groupComponentContainer);

            YogaNode scrollableContainer = new YogaNode();
            emptySpace.addChild(scrollableContainer);
            scrollableContainer.styleSetFlexGrow(1);
            scrollableContainer.styleSetFlexShrink(1);
            scrollableContainer.styleSetFlexDirection(FlexDirection.COLUMN);

            scrollableContainer.addChild(this.spawnGroupContainer = new SpawnGroupContainer(this));
            scrollableContainer.enableVerticalScrollbar();
            scrollableContainer.setShouldScissorChildren(true);
        }

        /*{
            YogaNode bottom = new YogaNode();
            bottom.styleSetWidthPercent(100);
            bottom.styleSetHeight(20);
            bottom.styleSetJustifyContent(Yoga.YGJustifyCenter);
            this.addChild(bottom);

            bottom.addChild(new SelectorButton(() -> "Select All", () -> false, () -> {
                entityTypeColorValue.addAllInGroup(this.selectedGroup);
            }));
            bottom.addChild(new SelectorButton(() -> "Deselect All", () -> false, () -> {
                entityTypeColorValue.removeAllInGroup(this.selectedGroup);
            }));
        }*/

        this.changeContents(SpawnGroup.values()[0]);
    }

    public void changeContents(SpawnGroup spawnGroup) {
        if (spawnGroup == null) {//all
            this.selectedIndex = 0;
        } else {
            this.selectedIndex = spawnGroup.ordinal() + 1;
        }

        groupComponentContainer.removeAllChildren();
        if (spawnGroup != null) {
            groupComponentContainer.addChild(new GroupComponent(this.getEntityTypeColorValue(), spawnGroup));
        }

        this.spawnGroupContainer.changeSpawnGroup(spawnGroup);
        this.spawnGroupContainer.refreshSearch();
    }
}
