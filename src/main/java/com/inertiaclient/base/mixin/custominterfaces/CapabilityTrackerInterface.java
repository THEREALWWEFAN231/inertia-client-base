package com.inertiaclient.base.mixin.custominterfaces;

public interface CapabilityTrackerInterface {

    boolean getState();

    void forceSetState(boolean state);

}
