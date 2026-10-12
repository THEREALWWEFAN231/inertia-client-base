package com.inertiaclient.base.render.yoga;

import org.lwjgl.util.yoga.Yoga;

//isn't an actual config for yoga, just sets defaults
public class YogaConfig {

    public static void initialize() {
        Yoga.YGConfigSetUseWebDefaults(Yoga.YGConfigGetDefault(), true);
    }

    public static void setPointScaleFactor(float scaleFactor) {
        Yoga.YGConfigSetPointScaleFactor(Yoga.YGConfigGetDefault(), scaleFactor);
    }

}
