package com.inertiaclient.base.gui.components.module.values.blockentitycolor;

import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.components.NativeRenderComponent;
import com.inertiaclient.base.gui.components.minecraftitems.ItemReferenceNode;
import com.inertiaclient.base.gui.components.minecraftitems.ItemRenderComponent;
import com.inertiaclient.base.gui.components.module.values.blockentity.BlockEntityTypePage;
import com.inertiaclient.base.gui.components.module.values.color.ColorContainer;
import com.inertiaclient.base.gui.components.module.values.color.ColorContainerInterface;
import com.inertiaclient.base.gui.components.tabbedpage.AbstractWrappedListContainer;
import com.inertiaclient.base.render.skia.instances.SkiaNativeRender;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.Display;
import com.inertiaclient.base.value.WrappedColor;
import com.inertiaclient.base.value.impl.BlockEntityColorValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.awt.Color;

public class BlockEntityColorsPage extends AbstractWrappedListContainer<NativeRenderComponent> {

    public BlockEntityColorsPage(BlockEntityColorValue blockEntityColorValue) {
        this.getListNode().setBeforeNativeRenderCallback((graphics, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            this.getListNode().getChildren().forEach(container -> {
                var blockEntityDisplay = container.getChildAtIndex(0);
                if (blockEntityDisplay.isHoveredAndInsideParent(globalMouseX, globalMouseY)) {
                    canvas.drawRect(container.getRelativeX() + blockEntityDisplay.getRelativeX(), container.getRelativeY() + blockEntityDisplay.getRelativeY(), blockEntityDisplay.getWidth(), blockEntityDisplay.getHeight(), new Color(255, 255, 255, 100));
                }
            });
        });
        this.setScrolledCallback((scrollReason, scrollDirection, relativeMouseX, relativeMouseY, amount) -> {
            this.getListNode().getSkiaNativeRender().getFrameBuffer().forceUpdateNextFrame();
        });

        for (BlockEntityType<?> blockEntityType : BuiltInRegistries.BLOCK_ENTITY_TYPE.stream().toList()) {

            YogaNode container = new YogaNode();
            this.getListNode().addChild(container);
            container.setHoverCursorToIndicateClick();
            String id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType).toLanguageKey();
            container.setSearchContext(id);
            container.setTooltip(() -> id);
            container.setTooltipDelay(() -> 0L);

            Block blockForBlockEntity = BlockEntityTypePage.getDisplayBlockForBlockEntity(blockEntityType);
            var blockEntityDisplay = new ItemReferenceNode(blockForBlockEntity.asItem());
            container.addChild(blockEntityDisplay);
            container.addChild(new ColorDisplay(blockEntityColorValue, blockEntityType));

            //dont use the displayed block/item tool tip
            blockEntityDisplay.setTooltip(null);
            blockEntityDisplay.setSearchContext(id);

            blockEntityDisplay.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
                if (button == ButtonIdentifier.LEFT) {
                    ModernClickGui.MODERN_CLICK_GUI.getRoot().addChild(new ColorContainer(blockEntityColorValue.getColorForBlockEntity(blockEntityType), new ColorContainerInterface() {
                        @Override
                        public String getNameHeader() {
                            return id;
                        }

                        @Override
                        public WrappedColor getDefault() {
                            return blockEntityColorValue.getColorFromHashMap(blockEntityColorValue.getDefaultValue(), blockEntityType);
                        }

                        @Override
                        public void setColor(WrappedColor wrappedColor) {
                            blockEntityColorValue.setColorForBlockEntity(blockEntityType, wrappedColor);
                        }
                    }, () -> blockEntityDisplay.getGlobalX() + relativeMouseX, () -> blockEntityDisplay.getGlobalY()));
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public NativeRenderComponent createListNodeType() {
        SkiaNativeRender nativeRender = new SkiaNativeRender().setSetNativeRender(guiGraphicsExtractor -> {
            this.getListNode().getChildren().forEach(container -> {

                var blockEntityDisplay = container.getChildAtIndex(0);
                if (blockEntityDisplay.getDisplay() == Display.FLEX && blockEntityDisplay instanceof ItemReferenceNode refNode && (this.getListNode().isChildInRenderBounds(blockEntityDisplay) || true)) {
                    guiGraphicsExtractor.pose().pushMatrix();
                    guiGraphicsExtractor.pose().translate(container.getRelativeX(), container.getRelativeY());
                    guiGraphicsExtractor.pose().translate(blockEntityDisplay.getRelativeX(), blockEntityDisplay.getRelativeY());
                    ItemRenderComponent.renderItemForGui(new ItemStack(refNode.getItem()), guiGraphicsExtractor);
                    guiGraphicsExtractor.pose().popMatrix();
                }
            });
        });
        nativeRender.getFrameBuffer().setFps(() -> 20);
        return new NativeRenderComponent(nativeRender);
    }

}
