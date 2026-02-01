package com.inertiaclient.base.gui.components.tabbedpage.impl;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public class BlockRenderComponent extends ItemRenderComponent {

    public BlockRenderComponent(Block block) {
        super(block.asItem());

        String id = BuiltInRegistries.BLOCK.wrapAsHolder(block).getRegisteredName();
        this.setSearchContext(id);
        this.setTooltip(() -> id);
    }
}
