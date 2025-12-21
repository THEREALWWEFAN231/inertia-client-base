package com.inertiaclient.base.gui.components.module.values.blocks;

import com.inertiaclient.base.gui.components.tabbedpage.BlockRenderComponent;
import com.inertiaclient.base.gui.components.tabbedpage.HashsetPage;
import com.inertiaclient.base.value.HashsetValue;
import net.minecraft.world.level.block.Block;

public class BlocksValuePage extends HashsetPage<Block> {

    public BlocksValuePage(HashsetValue<Block> hashsetValue) {
        super(hashsetValue, block -> new BlockRenderComponent(block));
    }

}
