package com.inertiaclient.base.gui.components.module.values._boolean;

import com.inertiaclient.base.gui.components.module.values.ValueYogaNode;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.value.impl.BooleanValue;

public class ValueBooleanComponent extends ValueYogaNode<BooleanValue> {

    private BooleanValue booleanValue;

    public ValueBooleanComponent(BooleanValue booleanValue) {
        super(booleanValue);

        this.booleanValue = booleanValue;

        //adds crap to this
        new BooleanComponent(booleanValue::getNameString, booleanValue::getValue, booleanValue::setValue, this);
    }

    protected YogaNode createAdvancedInfoContainer(float relativeMouseX, float relativeMouseY) {
        return new BooleanAdvancedInfo(this.booleanValue, () -> this.getGlobalX() + relativeMouseX, () -> this.getGlobalY());
    }
}
