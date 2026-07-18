package com.inertiaclient.base.value.impl;

import com.inertiaclient.base.value.RegistryHashsetValue;
import com.inertiaclient.base.value.group.ValueGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

import java.util.Collection;
import java.util.HashSet;

public class BlockEntityValue extends RegistryHashsetValue<BlockEntityType<?>> {

    public BlockEntityValue(String id, ValueGroup parent, HashSet<BlockEntityType<?>> defaultValue) {
        super(id, parent, defaultValue);
    }

    public BlockEntityValue(String id, ValueGroup parent, Collection<BlockEntityType<?>> defaultValue) {
        super(id, parent, defaultValue);
    }

    public BlockEntityValue(String id, ValueGroup parent, BlockEntityType<?>... defaultValue) {
        super(id, parent, defaultValue);
    }

    public BlockEntityValue(String id, ValueGroup parent) {
        super(id, parent);
    }

    @Override
    protected Registry<BlockEntityType<?>> getRegistry() {
        return BuiltInRegistries.BLOCK_ENTITY_TYPE;
    }

    public boolean isTargeted(BlockEntity blockEntity) {
        return this.isTargeted(blockEntity.getType());
    }

    public boolean isTargeted(BlockEntityType<?> blockEntityType) {
        return this.getValue().contains(blockEntityType);
    }

    public static HashSet<BlockEntityType<?>> getStorageContainers() {
        HashSet<BlockEntityType<?>> blockEntityTypes = new HashSet<>();

        blockEntityTypes.add(BlockEntityTypes.FURNACE);
        blockEntityTypes.add(BlockEntityTypes.CHEST);
        blockEntityTypes.add(BlockEntityTypes.TRAPPED_CHEST);
        blockEntityTypes.add(BlockEntityTypes.ENDER_CHEST);
        blockEntityTypes.add(BlockEntityTypes.DISPENSER);
        blockEntityTypes.add(BlockEntityTypes.DROPPER);
        blockEntityTypes.add(BlockEntityTypes.BREWING_STAND);
        blockEntityTypes.add(BlockEntityTypes.ENCHANTING_TABLE);
        blockEntityTypes.add(BlockEntityTypes.BEACON);
        blockEntityTypes.add(BlockEntityTypes.HOPPER);
        blockEntityTypes.add(BlockEntityTypes.SHULKER_BOX);
        blockEntityTypes.add(BlockEntityTypes.BARREL);
        blockEntityTypes.add(BlockEntityTypes.SMOKER);
        blockEntityTypes.add(BlockEntityTypes.BLAST_FURNACE);
        blockEntityTypes.add(BlockEntityTypes.CAMPFIRE);//idk if I want this as a storage container...
        blockEntityTypes.add(BlockEntityTypes.DECORATED_POT);
        blockEntityTypes.add(BlockEntityTypes.CRAFTER);

        return blockEntityTypes;
    }


}
