package com.inertiaclient.base.gui.components.module.values.blockentitycolor;

import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.PositionType;
import com.inertiaclient.base.render.yoga.layouts.YogaEdge;
import com.inertiaclient.base.value.impl.BlockEntityColorValue;
import net.minecraft.block.entity.BlockEntityType;

public class ColorDisplay extends YogaNode {

    public ColorDisplay(BlockEntityColorValue blockEntityColorValue, BlockEntityType<?> blockEntityType) {
        this.styleSetPositionType(PositionType.ABSOLUTE);
        this.styleSetPosition(YogaEdge.RIGHT, 0);
        this.styleSetPosition(YogaEdge.BOTTOM, 0);
        this.styleSetWidth(5);
        this.styleSetHeight(5);


        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            canvas.drawRRect(0, 0, this.getWidth(), this.getHeight(), 1, blockEntityColorValue.getColorForBlockEntity(blockEntityType).getRenderColor());
        });
    }

}
