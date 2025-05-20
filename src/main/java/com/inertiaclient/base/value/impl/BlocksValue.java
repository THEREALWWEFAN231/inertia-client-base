package com.inertiaclient.base.value.impl;

import com.inertiaclient.base.value.RegistryHashsetValue;
import com.inertiaclient.base.value.group.ValueGroup;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.Collection;
import java.util.HashSet;

public class BlocksValue extends RegistryHashsetValue<Block> {

    public BlocksValue(String id, ValueGroup parent, HashSet<Block> defaultValue) {
        super(id, parent, defaultValue);
    }

    public BlocksValue(String id, ValueGroup parent, Collection<Block> defaultValue) {
        super(id, parent, defaultValue);
    }

    public BlocksValue(String id, ValueGroup parent, Block... defaultValue) {
        super(id, parent, defaultValue);
    }

    public BlocksValue(String id, ValueGroup parent) {
        super(id, parent);
    }

    @Override
    protected Registry<Block> getRegistry() {
        return Registries.BLOCK;
    }

}
