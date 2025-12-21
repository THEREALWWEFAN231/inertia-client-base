package com.inertiaclient.base.gui.components.module.values.entitytype;

import com.inertiaclient.base.gui.components.SelectorButton;
import com.inertiaclient.base.gui.components.tabbedpage.WrappedListContainer;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.*;
import com.inertiaclient.base.value.impl.EntityTypeValue;
import lombok.Getter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.registries.BuiltInRegistries;

public class SpawnGroupContainer extends YogaNode {

    @Getter
    private WrappedListContainer wrappedListContainer;

    public SpawnGroupContainer(EntityTypeValue entityTypeValue, MobCategory spawnGroup) {
        this.styleSetFlexGrow(1);
        this.styleSetFlexShrink(1);
        this.styleSetFlexDirection(FlexDirection.COLUMN);


        this.wrappedListContainer = new WrappedListContainer();
        //wrappedListContainer.getListNode().styleSetJustifyContent(Yoga.YGJustifySpaceAround);
        this.addChild(wrappedListContainer);

        for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE.stream().toList()) {
            if (entityType.getCategory() == spawnGroup)
                wrappedListContainer.getListNode().addChild(new EntityTypeComponent(entityTypeValue, entityType));
        }

        {
            YogaNode bottom = new YogaNode();
            bottom.styleSetWidth(100, ExactPercentAuto.PERCENTAGE);
            bottom.styleSetHeight(14);
            bottom.styleSetFlexShrink(0);
            bottom.styleSetJustifyContent(JustifyContent.CENTER);
            bottom.styleSetGap(GapGutter.COLUMN, 5);
            bottom.addChild(new SelectorButton(() -> "Select All", () -> false, () -> {
                entityTypeValue.addAllInGroup(spawnGroup);
            }));
            bottom.addChild(new SelectorButton(() -> "Deselect All", () -> false, () -> {
                entityTypeValue.removeAllInGroup(spawnGroup);
            }));
            this.addChild(bottom);
        }

        this.setKeyPressedCallback((keyCode, scanCode, modifiers) -> {
            if (Screen.isSelectAll(keyCode)) {

                boolean isEverythingSelected = entityTypeValue.isAllInGroupSelected(spawnGroup);
                if (isEverythingSelected) {
                    entityTypeValue.removeAllInGroup(spawnGroup);
                } else {
                    entityTypeValue.addAllInGroup(spawnGroup);
                }

                return true;
            }
            return false;
        });
    }

}
