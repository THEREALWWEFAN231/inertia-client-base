package com.inertiaclient.base.gui.components.tabbedpage;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

public class BlockRenderComponent extends ItemRenderComponent {

    public BlockRenderComponent(Block block) {
        super(block.asItem());

        String id = BuiltInRegistries.BLOCK.wrapAsHolder(block).getRegisteredName();
        this.setSearchContext(id);
        this.setTooltip(() -> id);
    }
}
