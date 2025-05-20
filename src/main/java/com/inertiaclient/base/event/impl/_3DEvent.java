package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;

@AllArgsConstructor
public class _3DEvent extends Event {

    @Getter
    private MatrixStack matrices;
    @Getter
    private float tickDelta;

}
