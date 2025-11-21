package com.inertiaclient.base.gui.components.module.values.entitycolor;

import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.module.values.color.ColorContainer;
import com.inertiaclient.base.gui.components.module.values.color.ColorContainerInterface;
import com.inertiaclient.base.gui.components.module.values.entitytype.EntityTypeComponent;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.skia.SvgRenderer;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.FlexDirection;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;
import com.inertiaclient.base.render.yoga.layouts.YogaEdge;
import com.inertiaclient.base.value.WrappedColor;
import com.inertiaclient.base.value.impl.EntityTypeColorValue;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;

import java.awt.Color;

public class EntityTypeColorComponent extends YogaNode {

    private static SvgRenderer LINKED_SVG = new SvgRenderer("icb/textures/linked.svg");
    private EntityType entityType;

    public EntityTypeColorComponent(EntityTypeColorValue entityTypeColorValue, EntityType entityType) {
        this.entityType = entityType;
        this.setSearchContext(entityType.getName().getString());

        this.styleSetWidth(100);
        this.styleSetHeight(28);
        this.styleSetFlexDirection(FlexDirection.ROW);
        this.styleSetPadding(YogaEdge.ALL, 5);
        this.styleSetGap(GapGutter.COLUMN, 2);
        this.setHoverCursorToIndicateClick();


        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), MainFrame.s_selectorButtonColor.get());
            try (var stroke = SkiaUtils.createStrokePaint(Color.black, MainFrame.s_lineWidth.get())) {
                canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), stroke);
            }
        });

        YogaNode left = new YogaNode();
        left.styleSetFlexShrink(0);
        this.addChild(left);

        YogaNode colorDisplay = new YogaNode();
        left.addChild(colorDisplay);
        colorDisplay.styleSetWidth(18);
        colorDisplay.styleSetHeight(18);
        colorDisplay.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            canvas.drawRect(0, 0, colorDisplay.getWidth(), colorDisplay.getHeight(), entityTypeColorValue.getValue().getColorForEntityType(entityType).getRenderColor());
            try (var stroke = SkiaUtils.createStrokePaint(Color.black, .3f)) {
                canvas.drawRect(0, 0, colorDisplay.getWidth(), colorDisplay.getHeight(), stroke);
            }
        });

        YogaNode right = new YogaNode();
        right.styleSetFlexGrow(1);
        right.styleSetFlexDirection(FlexDirection.COLUMN);
        this.addChild(right);

        right.addChild(new NameLabel(() -> entityType.getName().getString(), this));

        {
            YogaNode linkComponent = new YogaNode();
            linkComponent.styleSetFlexGrow(1);
            right.addChild(linkComponent);
            //linkComponent.styleSetWidth(12);
            // linkComponent.styleSetMargin(Yoga.YGEdgeRight, 5);
            linkComponent.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
                boolean isLinked = entityTypeColorValue.getValue().isLinked(entityType);
                try (var paint = SkiaUtils.createPaintForColor(isLinked ? EntityTypeComponent.selectedColor : EntityTypeComponent.unSelectedColor)) {
                    LINKED_SVG.render(canvas, 0, -2, 12, 12, paint);
                }

                if (isLinked) {
                    var linkedToText = CanvasWrapper.getFreshTextBuilder();
                    linkedToText.basic(Text.translatable("icb.gui.pages.entitycolor.linked_to", entityType.getSpawnGroup().getName()), 0, 8, this.isHoveredAndInsideParent(globalMouseX, globalMouseY) ? MainFrame.s_selectedTextColor.get() : MainFrame.s_unselectedTextColor.get());
                    linkedToText.setFontSize(4);
                    linkedToText.draw(canvas);
                }
            });
            linkComponent.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {

                if (!entityTypeColorValue.getValue().isLinked(entityType)) {
                    entityTypeColorValue.getValue().link(entityType);
                    return true;
                }
                return false;
            });
        }

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {

                WrappedColor spawnGroupColor = entityTypeColorValue.getValue().getSpawnGroupColor(entityType.getSpawnGroup());
                WrappedColor color = entityTypeColorValue.getValue().getIndividualColor(entityType);
                if (color == null) {//it's linked, we are going to create a color and unlink it
                    color = spawnGroupColor;
                    entityTypeColorValue.getValue().setIndividualColor(entityType, color);
                }
                ModernClickGui.MODERN_CLICK_GUI.getRoot().addChild(new ColorContainer(color, new ColorContainerInterface() {
                    @Override
                    public String getNameHeader() {
                        return entityType.getTranslationKey();
                    }

                    @Override
                    public WrappedColor getDefault() {
                        return spawnGroupColor;
                    }

                    @Override
                    public void setColor(WrappedColor wrappedColor) {
                        entityTypeColorValue.getValue().setIndividualColor(entityType, wrappedColor);
                    }
                }, () -> this.getGlobalX() + relativeMouseX, () -> this.getGlobalY()));
                return true;
            }

            return false;
        });
    }


}