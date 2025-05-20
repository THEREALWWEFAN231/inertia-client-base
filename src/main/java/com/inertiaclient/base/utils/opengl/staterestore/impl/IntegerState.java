package com.inertiaclient.base.utils.opengl.staterestore.impl;

import com.inertiaclient.base.utils.opengl.staterestore.OpenGLState;

import java.util.Stack;

public abstract class IntegerState implements OpenGLState {

    private final Stack<Integer> before = new Stack<>();

    protected abstract int getValue();

    protected abstract void restoreValue(int value);

    @Override
    public void cache() {
        this.before.push(this.getValue());
    }

    @Override
    public void restore() {
        this.restoreValue(this.before.pop());
    }
}
