package com.inertiaclient.base.render.animation.accessor;

import com.inertiaclient.base.render.animation.AnimationValue;
import dorkbox.tweenEngine.Tween;
import dorkbox.tweenEngine.TweenAccessor;

public abstract class AnimationType {

    public abstract TweenAccessor getAccessor();

    public Tween<AnimationValue> to(float duration) {
        return AnimationValue.tweenEngine.to(this, 0, getAccessor(), duration);
    }

    public Tween<AnimationValue> from(float duration) {
        return AnimationValue.tweenEngine.from(this, 0, getAccessor(), duration);
    }

    public Tween<AnimationValue> set() {
        return AnimationValue.tweenEngine.set(this, 0, getAccessor());
    }

}
