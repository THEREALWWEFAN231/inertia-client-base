package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

@RequiredArgsConstructor
public class BlockCollisionShapeEvent extends Event {


    @NonNull
    @Getter
    private BlockGetter world;
    @NonNull
    @Getter
    private BlockPos pos;
    @NonNull
    @Getter
    private CollisionContext context;
    @NonNull
    @Getter
    private BlockState blockState;
    @Getter
    @Setter
    private VoxelShape voxelShape;

}
