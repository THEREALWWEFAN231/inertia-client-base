package com.inertiaclient.base.gui.components.minecraftitems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public class BlockRenderComponent extends ItemRenderComponent {

    public BlockRenderComponent(Block block) {
        super(block.asItem(), BuiltInRegistries.BLOCK.wrapAsHolder(block).getRegisteredName());
    }
}
