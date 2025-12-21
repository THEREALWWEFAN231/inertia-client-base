package com.inertiaclient.base.gui.components.module.values.blockentity;

import com.inertiaclient.base.gui.components.tabbedpage.HashsetPage;
import com.inertiaclient.base.gui.components.tabbedpage.ItemRenderComponent;
import com.inertiaclient.base.value.HashsetValue;
import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Optional;
import java.util.Set;

public class BlockEntityTypePage extends HashsetPage<BlockEntityType<?>> {

    public BlockEntityTypePage(HashsetValue<BlockEntityType<?>> hashsetValue) {
        super(hashsetValue, blockEntityType -> {
            Block blockForBlockEntity = BlockEntityTypePage.getBlockFromBlockEntity(blockEntityType);
            String id = BlockEntityType.getKey(blockEntityType).toLanguageKey();
            var blockComponent = new ItemRenderComponent(blockForBlockEntity.asItem());

            blockComponent.setSearchContext(id);
            blockComponent.setTooltip(() -> id);
            return blockComponent;
        });
    }

    public static Block getBlockFromBlockEntity(BlockEntityType<?> blockEntityType) {
        Set<Block> blocks = ((BlockEntityTypeAccessor) blockEntityType).getBlocks();
        Optional<Block> blockToRender = blocks.stream().findFirst();
        if (!blockToRender.isEmpty()) {
            return blockToRender.get();
        }
        return Blocks.AIR;
    }
}
