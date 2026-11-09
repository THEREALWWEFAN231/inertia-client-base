package com.inertiaclient.base.value.group;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.utils.LanguageConstants;

public class HudValueGroup extends ValueGroup {

    public HudValueGroup(String id) {
        super(id, null);

        this.setSaveHandler(() -> InertiaBase.instance.getFileManager().getHudGroupQueuedSave().queue());
    }

    @Override
    public String getLanguageBaseKey() {
        return LanguageConstants.GLOBAL_HUD_GROUP_KEY + "." + this.getId();
    }

}
