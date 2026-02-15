package com.inertiaclient.base.gui.components.module.values.entitytype;

import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.value.impl.EntityTypeValue;
import net.minecraft.world.entity.EntityType;

import java.awt.Color;

public class EntityTypeComponent extends YogaNode {

    private EntityType entityType;

    public static Color selectedColor = new Color(79, 138, 47);
    public static Color unSelectedColor = new Color(143, 47, 47);

    public EntityTypeComponent(EntityTypeValue entityTypeValue, EntityType entityType) {
        this.entityType = entityType;
        this.setSearchContext(entityType.getDescription().getString());

        this.styleSetHeight(12);
        this.setHoverCursorToIndicateClick();
        //this.setDebug(true);
        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var name = CanvasWrapper.getFreshTextBuilder();
            name.basic(entityType.getDescription().getString(), 0, this.getHeight() / 2, entityTypeValue.getValue().contains(entityType) ? EntityTypeComponent.selectedColor : EntityTypeComponent.unSelectedColor);
            name.setFontSize(6);
            name.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE).setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.LEFT);
            name.draw(canvas);
        });

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {

            if (button == ButtonIdentifier.LEFT) {
                if (entityTypeValue.getValue().contains(entityType)) {
                    entityTypeValue.removeHard(entityType);
                } else {
                    entityTypeValue.addHard(entityType);
                }
                return true;
            }

            return false;
        });

        this.setPreLayoutCalculationsCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var name = CanvasWrapper.getFreshTextBuilder();
            name.setText(entityType.getDescription().getString());
            name.setFontSize(6);

            this.styleSetWidth(name.getTextWidth());
        });
    }


}