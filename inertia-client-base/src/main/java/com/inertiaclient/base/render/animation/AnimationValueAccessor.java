package com.inertiaclient.base.render.animation;

import dorkbox.tweenEngine.TweenAccessor;
import org.jetbrains.annotations.NotNull;

public class AnimationValueAccessor implements TweenAccessor<AnimationValue> {

    @Override
    public int getValues(AnimationValue animationValue, int i, @NotNull float[] floats) {
        floats[0] = animationValue.getValue();
        return 1;
    }

    @Override
    public void setValues(AnimationValue animationValue, int i, @NotNull float[] floats) {
        animationValue.setValue(floats[0]);
    }
}
