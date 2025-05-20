package com.inertiaclient.base.gui.components.hudeditor;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.components.helpers.VerticalListContainer;
import com.inertiaclient.base.hud.HudComponent;

public class HudEditorPage extends VerticalListContainer {

    public HudEditorPage() {
        for (HudComponent element : InertiaBase.instance.getHudManager().getComponents()) {
            this.addToList(new HudElementComponent(element));
        }
    }

}
