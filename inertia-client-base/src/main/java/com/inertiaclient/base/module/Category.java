package com.inertiaclient.base.module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.chat.Component;

@AllArgsConstructor
public enum Category {
    Combat(Component.translatable("icb.category.combat")), Player(Component.translatable("icb.category.player")), Movement(Component.translatable("icb.category.movement")), Render(Component.translatable("icb.category.render")), World(Component.translatable("icb.category.world")), Other(Component.translatable("icb.category.other"));

    @Getter
    private Component renderName;

}
