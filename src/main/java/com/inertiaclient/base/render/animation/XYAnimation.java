package com.inertiaclient.base.render.animation;

import lombok.Getter;
import lombok.Setter;

public class XYAnimation {

    public static final int X = 0;
    public static final int Y = 1;
    public static final int XY = 2;


    @Getter
    @Setter
    private float x;
    @Getter
    @Setter
    private float y;

}
