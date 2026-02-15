package com.inertiaclient.modtemplate;

import com.inertiaclient.base.hud.HudComponent;
import com.inertiaclient.base.hud.HudGroup;
import com.inertiaclient.base.hud.HudManager;
import com.inertiaclient.modtemplate.hudcomponents.FPS;

import java.util.ArrayList;

public class HudComponents {

    public void initialize(ArrayList<HudComponent> components, ArrayList<HudGroup> groups, HudManager hudManager) {
        //components.add(new FPS());
        components.add(hudManager.getBottomLeft().addComponent(new FPS().setEnabledOnInit(true)));
    }

}
