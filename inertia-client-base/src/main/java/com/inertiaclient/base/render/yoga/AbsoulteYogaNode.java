package com.inertiaclient.base.render.yoga;

import com.inertiaclient.base.render.yoga.layouts.PositionType;
import lombok.Setter;

public class AbsoulteYogaNode extends YogaNode {

    @Setter
    private float x = -999;
    @Setter
    private float y = -999;
    private float width = -999;
    private float height = -999;

    public AbsoulteYogaNode() {
        this.styleSetPositionType(PositionType.ABSOLUTE);
    }

    public float _relativeX() {
        return x == -999 ? super._relativeX() : x;
    }

    public float _relativeY() {
        return y == -999 ? super._relativeY() : y;
    }

    public void setWidth(float width) {
        if (this.width != width) {
            this.styleSetWidth(width);
            super.width = width;
        }
        this.width = width;
    }

    public void setHeight(float height) {
        if (this.height != height) {
            this.styleSetHeight(height);
            super.height = height;
        }
        this.height = height;
    }

    protected float width() {
        if (this.width == -999) {
            return super.width();
        }
        return this.width;
    }

    protected float height() {
        if (this.height == -999) {
            return super.height();
        }
        return this.height;
    }

}
