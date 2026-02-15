package com.inertiaclient.base.event;

import lombok.Setter;

public class Event {

    @Setter
    private boolean cancelled;

    public boolean isCancelled() {
        return this.cancelled;
    }

}
