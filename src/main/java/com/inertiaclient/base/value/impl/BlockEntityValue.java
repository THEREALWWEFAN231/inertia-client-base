package com.inertiaclient.base.value.impl;

import com.inertiaclient.base.value.RegistryHashsetValue;
import com.inertiaclient.base.value.group.ValueGroup;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

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
        return Registries.BLOCK_ENTITY_TYPE;
    }

    public boolean isTargeted(BlockEntity blockEntity) {
        return this.isTargeted(blockEntity.getType());
    }

    public boolean isTargeted(BlockEntityType<?> blockEntityType) {
        return this.getValue().contains(blockEntityType);
    }

    public static HashSet<BlockEntityType<?>> getStorageContainers() {
        HashSet<BlockEntityType<?>> blockEntityTypes = new HashSet<>();

        blockEntityTypes.add(BlockEntityType.FURNACE);
        blockEntityTypes.add(BlockEntityType.CHEST);
        blockEntityTypes.add(BlockEntityType.TRAPPED_CHEST);
        blockEntityTypes.add(BlockEntityType.ENDER_CHEST);
        blockEntityTypes.add(BlockEntityType.DISPENSER);
        blockEntityTypes.add(BlockEntityType.DROPPER);
        blockEntityTypes.add(BlockEntityType.BREWING_STAND);
        blockEntityTypes.add(BlockEntityType.ENCHANTING_TABLE);
        blockEntityTypes.add(BlockEntityType.BEACON);
        blockEntityTypes.add(BlockEntityType.HOPPER);
        blockEntityTypes.add(BlockEntityType.SHULKER_BOX);
        blockEntityTypes.add(BlockEntityType.BARREL);
        blockEntityTypes.add(BlockEntityType.SMOKER);
        blockEntityTypes.add(BlockEntityType.BLAST_FURNACE);
        blockEntityTypes.add(BlockEntityType.CAMPFIRE);//idk if I want this as a storage container...
        blockEntityTypes.add(BlockEntityType.DECORATED_POT);
        blockEntityTypes.add(BlockEntityType.CRAFTER);

        return blockEntityTypes;
    }


}
