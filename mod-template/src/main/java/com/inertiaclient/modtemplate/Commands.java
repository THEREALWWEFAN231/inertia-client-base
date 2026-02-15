package com.inertiaclient.modtemplate;

import com.inertiaclient.base.command.Command;
import com.inertiaclient.modtemplate.commands.ExampleCommand;

import java.util.ArrayList;

public class Commands {

    public void initialize(ArrayList<Command> commands) {
        commands.add(new ExampleCommand());
    }

}
