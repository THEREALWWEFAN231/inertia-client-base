package com.inertiaclient.base.value.impl;

import com.inertiaclient.base.value.group.ValueGroup;

public class MaxFPSValue extends IntegerValue {

    public MaxFPSValue(String id, ValueGroup parent, int defaultValue) {
        super(id, parent, defaultValue, 1, 360);
    }

    //unlimited
    public MaxFPSValue(String id, ValueGroup parent) {
        this(id, parent, 360);
    }

    public int getFpsForCache() {
        return getFpsForCache(this.getValue());
    }

    private static int getFpsForCache(int value) {
        if (value <= 0 || value >= 360) {
            return -1;
        }
        return value;
    }

    @Override
    public String getGuiValueString(Integer number) {
        if (getFpsForCache() == -1) {
            return "Unlimited";
        }
        return super.getGuiValueString(number);
    }
}
