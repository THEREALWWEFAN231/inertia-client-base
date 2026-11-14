package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.level.ChunkPos;

@AllArgsConstructor
public class ChunkUnloadEvent extends Event {

    @Getter
    private ChunkPos pos;
}
