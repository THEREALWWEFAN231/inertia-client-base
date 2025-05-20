package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResolutionChangeEvent extends Event {

    private Type type;
    //frame buffer
    private int windowFBWidth;
    private int windowFBHeight;

    public enum Type {
        PRE, POST;
    }

}
