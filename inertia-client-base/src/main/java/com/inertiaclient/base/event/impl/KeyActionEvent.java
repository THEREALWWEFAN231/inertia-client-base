package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.Getter;

public class KeyActionEvent extends Event {

    @Getter
    private final int key;
    @Getter
    private final int scanCode;
    @Getter
    private final boolean isPressAction;
    @Getter
    private final int glfwAction;

    public KeyActionEvent(int key, int scanCode, boolean isPressAction, int glfwAction) {
        this.key = key;
        this.scanCode = scanCode;
        this.isPressAction = isPressAction;
        this.glfwAction = glfwAction;
    }
}
