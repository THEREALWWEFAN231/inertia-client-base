package com.inertiaclient.base.module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.Text;

@AllArgsConstructor
public enum Category {
    Combat(Text.translatable("icb.category.combat")), Player(Text.translatable("icb.category.player")), Movement(Text.translatable("icb.category.movement")), Render(Text.translatable("icb.category.render")), World(Text.translatable("icb.category.world")), Other(Text.translatable("icb.category.other"));

    @Getter
    private Text renderName;

}
