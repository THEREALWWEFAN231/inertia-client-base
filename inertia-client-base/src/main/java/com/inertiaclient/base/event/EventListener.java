package com.inertiaclient.base.event;

public interface EventListener<E extends Event> {

    void handle(E event);
}