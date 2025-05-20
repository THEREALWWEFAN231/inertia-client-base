package com.inertiaclient.base.event.impl.inertia;

import com.inertiaclient.base.event.Event;
import com.inertiaclient.base.module.Module;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ModuleToggledEvent extends Event {

    private Module module;

}
