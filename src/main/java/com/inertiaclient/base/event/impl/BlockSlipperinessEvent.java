package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.level.block.Block;

@AllArgsConstructor
public class BlockSlipperinessEvent extends Event {

    @Getter
    private Block block;
    @Getter
    @Setter
    private float slipperiness;

}
