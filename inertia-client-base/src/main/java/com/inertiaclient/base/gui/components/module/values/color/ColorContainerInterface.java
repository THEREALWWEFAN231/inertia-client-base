package com.inertiaclient.base.gui.components.module.values.color;

import com.inertiaclient.base.value.WrappedColor;

public interface ColorContainerInterface {

    String getNameHeader();

    WrappedColor getDefault();

    void setColor(WrappedColor wrappedColor);

}
