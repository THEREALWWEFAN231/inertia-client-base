package com.inertiaclient.base.value.group;

import com.inertiaclient.base.InertiaBase;

public class HudValueGroup extends ValueGroup {

    public HudValueGroup(String id) {
        super(id, null);

        this.setSaveHandler(() -> InertiaBase.instance.getFileManager().getHudQueuedSave().queue());
    }

    @Override
    public String getLanguageBaseKey() {
        return "icb.hudgroup.values_group";
    }

}
