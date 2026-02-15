package com.inertiaclient.base;

import net.fabricmc.api.ClientModInitializer;

public class FabricMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        InertiaBase.instance.initialize();
    }
}
