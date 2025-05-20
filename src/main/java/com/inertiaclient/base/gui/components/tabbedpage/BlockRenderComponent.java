package com.inertiaclient.base.gui.components.tabbedpage;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

public class BlockRenderComponent extends ItemRenderComponent {

    public BlockRenderComponent(Block block) {
        super(block.asItem());

        String id = Registries.BLOCK.getEntry(block).getIdAsString();
        this.setSearchContext(id);
        this.setTooltip(() -> id);
    }
}
