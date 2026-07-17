package com.inertiaclient.modtemplate;

import com.inertiaclient.base.command.Command;
import com.inertiaclient.base.command.CommandManager;
import com.inertiaclient.base.hud.HudComponent;
import com.inertiaclient.base.hud.HudGroup;
import com.inertiaclient.base.hud.HudManager;
import com.inertiaclient.base.mods.InertiaMod;
import com.inertiaclient.base.module.Module;
import com.inertiaclient.base.module.ModuleManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ModTemplate extends InertiaMod {

    public static ModTemplate instance;

    public static final Logger LOGGER = LoggerFactory.getLogger("icb-mod-template");

    @Getter
    private Modules modules;
    @Getter
    private HudComponents hudComponents;
    @Getter
    private Commands commands;


    public ModTemplate() {
        instance = this;

        this.modules = new Modules();
        this.hudComponents = new HudComponents();
        this.commands = new Commands();
    }

    @Override
    public void initialize() {
        System.out.println("ModTemplate initialized");
    }

    @Override
    public void initializeModules(ArrayList<Module> modules, ModuleManager moduleManager) {
        this.modules.initialize(modules);
    }

    @Override
    public void initializeHudComponents(ArrayList<HudComponent> components, ArrayList<HudGroup> groups, HudManager hudManager) {
        this.hudComponents.initialize(components, groups, hudManager);
    }

    @Override
    public void initializeCommands(ArrayList<Command> commands, CommandManager commandManager) {
        this.commands.initialize(commands);
    }

}
