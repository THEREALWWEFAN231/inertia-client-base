package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

@AllArgsConstructor
public class EntityLabelRenderEvent extends Event {

    @Getter
    private Entity entity;
    @Getter
    private EntityRenderState entityRenderState;
    @Getter
    private Text label;

}
