package com.inertiaclient.base.gui.components.minecraftitems;

import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public class BlockReferenceNode extends ItemReferenceNode {

    @Getter
    private Block block;

    public BlockReferenceNode(Block block) {
        super(block.asItem(), BuiltInRegistries.BLOCK.wrapAsHolder(block).getRegisteredName());

        this.block = block;
    }
}
