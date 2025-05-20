package com.inertiaclient.base.mods;

import com.inertiaclient.base.command.Command;
import com.inertiaclient.base.command.CommandManager;
import com.inertiaclient.base.hud.HudComponent;
import com.inertiaclient.base.hud.HudGroup;
import com.inertiaclient.base.hud.HudManager;
import com.inertiaclient.base.module.Module;
import com.inertiaclient.base.module.ModuleManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;

public abstract class InertiaMod {

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String name;
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String id;

    public abstract void initialize();


    public abstract void initializeModules(ArrayList<Module> modules, ModuleManager moduleManager);

    public abstract void initializeHudComponents(ArrayList<HudComponent> components, ArrayList<HudGroup> groups, HudManager hudManager);

    public abstract void initializeCommands(ArrayList<Command> commands, CommandManager commandManager);

    public void onConfigSaved(File configFolder) {

    }

    public void onConfigLoaded(File configFolder) {

    }
}
