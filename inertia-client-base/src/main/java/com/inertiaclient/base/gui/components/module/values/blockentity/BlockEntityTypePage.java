package com.inertiaclient.base.gui.components.module.values.blockentity;

import com.inertiaclient.base.gui.components.minecraftitems.ItemRenderComponent;
import com.inertiaclient.base.gui.components.tabbedpage.impl.HashsetPage;
import com.inertiaclient.base.value.HashsetValue;
import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

import java.util.Optional;
import java.util.Set;

public class BlockEntityTypePage extends HashsetPage<BlockEntityType<?>> {

    public BlockEntityTypePage(HashsetValue<BlockEntityType<?>> hashsetValue) {
        super(hashsetValue, blockEntityType -> {
            Block blockForBlockEntity = BlockEntityTypePage.getDisplayBlockForBlockEntity(blockEntityType);
            String id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType).toLanguageKey();
            return new ItemRenderComponent(blockForBlockEntity.asItem(), id);
        });
    }

    public static Block getDisplayBlockForBlockEntity(BlockEntityType<?> blockEntityType) {
        //few overrides, the Sets don't keep order, I don't want chest to be a copper chest, a sign to be a random sign
        if (blockEntityType == BlockEntityTypes.CHEST) {
            return Blocks.CHEST;
        }
        if (blockEntityType == BlockEntityTypes.SIGN) {
            return Blocks.OAK_SIGN;
        }
        if (blockEntityType == BlockEntityTypes.HANGING_SIGN) {
            return Blocks.OAK_HANGING_SIGN;
        }
        if (blockEntityType == BlockEntityTypes.SKULL) {
            return Blocks.SKELETON_SKULL;
        }
        if (blockEntityType == BlockEntityTypes.SHELF) {
            return Blocks.OAK_SHELF;
        }
        if (blockEntityType == BlockEntityTypes.COPPER_GOLEM_STATUE) {
            return Blocks.COPPER_GOLEM_STATUE.weathering().unaffected();
        }
        if (blockEntityType == BlockEntityTypes.BANNER) {
            return Blocks.BANNER.white();
        }
        if (blockEntityType == BlockEntityTypes.SHULKER_BOX) {
            return Blocks.SHULKER_BOX;
        }
        if (blockEntityType == BlockEntityTypes.CAMPFIRE) {
            return Blocks.CAMPFIRE;
        }
        if (blockEntityType == BlockEntityTypes.COMMAND_BLOCK) {
            return Blocks.COMMAND_BLOCK;
        }
        if (blockEntityType == BlockEntityTypes.BRUSHABLE_BLOCK) {
            return Blocks.SUSPICIOUS_SAND;
        }

        Set<Block> blocks = ((BlockEntityTypeAccessor) blockEntityType).getBlocks();
        Optional<Block> blockToRender = blocks.stream().findFirst();
        if (!blockToRender.isEmpty()) {
            return blockToRender.get();
        }
        return Blocks.AIR;
    }
}
