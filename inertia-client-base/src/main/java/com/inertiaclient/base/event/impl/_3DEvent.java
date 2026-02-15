package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.mojang.blaze3d.vertex.PoseStack;

@AllArgsConstructor
public class _3DEvent extends Event {

    @Getter
    private PoseStack matrices;
    @Getter
    private float tickDelta;

}
