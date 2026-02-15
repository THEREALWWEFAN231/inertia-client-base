package com.inertiaclient.base.render.animation;

import dorkbox.tweenEngine.TweenEquation;
import org.jetbrains.annotations.NotNull;

public abstract class CustomTweenEquation implements TweenEquation {

    private String name;

    public CustomTweenEquation(String name) {
        this.name = name;

        Animations.ALL_ANIMATIONS.add(this);
        Animations.ANIMATIONS_BY_NAME.put(this.name, this);
    }

    @NotNull
    @Override
    public String name() {
        return this.name;
    }
}
