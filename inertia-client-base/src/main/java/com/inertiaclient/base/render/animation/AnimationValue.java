package com.inertiaclient.base.render.animation;

import dorkbox.tweenEngine.Tween;
import dorkbox.tweenEngine.TweenEngine;
import lombok.Getter;
import lombok.Setter;

public class AnimationValue {

    public static final AnimationValueAccessor ACCESSOR = new AnimationValueAccessor();
    public static TweenEngine tweenEngine = TweenEngine.Companion.create()/*.setWaypointsLimit(4)*/.registerAccessor(AnimationValue.class, ACCESSOR).registerAccessor(XYAnimation.class, XYAccessor.ACCESSOR).build();//registerAccessor is not necessary if we specify the accessor inn the to/from/set etc methods, but we do it anyway...

    @Getter
    @Setter
    private float value;
    @Getter
    private Tween<AnimationValue> cachedTween;

    public Tween<AnimationValue> to(float duration) {
        return cachedTween = tweenEngine.to(this, 0, ACCESSOR, duration).ease(Animations.linear);
    }

    public Tween<AnimationValue> from(float duration) {
        return tweenEngine.from(this, 0, ACCESSOR, duration);
    }

    public Tween<AnimationValue> set() {
        return tweenEngine.set(this, 0, ACCESSOR);
    }

    public void cancelThis() {
        AnimationValue.tweenEngine.cancelTarget(this);
    }

}
