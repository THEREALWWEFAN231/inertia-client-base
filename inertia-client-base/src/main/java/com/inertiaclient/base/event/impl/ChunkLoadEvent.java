package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LevelChunk;

@AllArgsConstructor
public class ChunkLoadEvent extends Event {

    @Getter
    private LevelChunk chunk;
    @Getter
    private ClientLevel world;
}
