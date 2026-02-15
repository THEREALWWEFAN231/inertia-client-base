package com.inertiaclient.base.render.animation;

import dorkbox.tweenEngine.TweenAccessor;
import org.jetbrains.annotations.NotNull;

public class XYAccessor implements TweenAccessor<XYAnimation> {

    public static final XYAccessor ACCESSOR = new XYAccessor();

    @Override
    public int getValues(XYAnimation xyAnimation, int i, @NotNull float[] floats) {

        switch (i) {
            case XYAnimation.X:
                floats[0] = xyAnimation.getX();
                return 1;
            case XYAnimation.Y:
                floats[0] = xyAnimation.getY();
                return 1;
            case XYAnimation.XY:
                floats[0] = xyAnimation.getX();
                floats[1] = xyAnimation.getY();
                return 2;
            default: {
                return -1;
            }
        }
    }

    @Override
    public void setValues(XYAnimation xyAnimation, int i, @NotNull float[] floats) {
        switch (i) {
            case XYAnimation.X:
                xyAnimation.setX(floats[0]);
                break;
            case XYAnimation.Y:
                xyAnimation.setY(floats[0]);
                break;
            case XYAnimation.XY:
                xyAnimation.setX(floats[0]);
                xyAnimation.setY(floats[1]);
                break;
        }
    }
}
