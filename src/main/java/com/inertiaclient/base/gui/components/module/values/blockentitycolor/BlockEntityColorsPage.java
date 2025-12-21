package com.inertiaclient.base.gui.components.module.values.blockentitycolor;

import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.components.module.values.blockentity.BlockEntityTypePage;
import com.inertiaclient.base.gui.components.module.values.color.ColorContainer;
import com.inertiaclient.base.gui.components.module.values.color.ColorContainerInterface;
import com.inertiaclient.base.gui.components.tabbedpage.ItemRenderComponent;
import com.inertiaclient.base.gui.components.tabbedpage.WrappedListContainer;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.value.WrappedColor;
import com.inertiaclient.base.value.impl.BlockEntityColorValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.BuiltInRegistries;

public class BlockEntityColorsPage extends WrappedListContainer {

    public BlockEntityColorsPage(BlockEntityColorValue blockEntityColorValue) {
        for (BlockEntityType<?> blockEntityType : BuiltInRegistries.BLOCK_ENTITY_TYPE.stream().toList()) {

            YogaNode container = new YogaNode();
            container.setHoverCursorToIndicateClick();
            this.getListNode().addChild(container);

            Block blockForBlockEntity = BlockEntityTypePage.getBlockFromBlockEntity(blockEntityType);
            var blockComponent = new ItemRenderComponent(blockForBlockEntity.asItem());
            container.addChild(blockComponent);
            container.addChild(new ColorDisplay(blockEntityColorValue, blockEntityType));

            String id = BlockEntityType.getKey(blockEntityType).toLanguageKey();
            blockComponent.setSearchContext(id);
            blockComponent.setTooltip(() -> id);
            blockComponent.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
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
                    }, () -> blockComponent.getGlobalX() + relativeMouseX, () -> blockComponent.getGlobalY()));
                    return true;
                }
                return false;
            });
        }
    }
}
