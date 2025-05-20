package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@RequiredArgsConstructor
public class BlockCollisionShapeEvent extends Event {


    @NonNull
    @Getter
    private BlockView world;
    @NonNull
    @Getter
    private BlockPos pos;
    @NonNull
    @Getter
    private ShapeContext context;
    @NonNull
    @Getter
    private BlockState blockState;
    @Getter
    @Setter
    private VoxelShape voxelShape;

}
