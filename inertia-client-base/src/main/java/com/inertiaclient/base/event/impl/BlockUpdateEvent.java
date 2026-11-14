package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@AllArgsConstructor
public class BlockUpdateEvent extends Event {

    @Getter
    private BlockPos pos;
    @Getter
    private BlockState oldState;
    @Getter
    private BlockState newState;

}
