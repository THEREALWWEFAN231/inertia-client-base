package com.inertiaclient.base.mods;

import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.ArrayList;

public class ModLoader {


    @Getter
    private ArrayList<InertiaMod> mods = new ArrayList<>();

    public ModLoader() {
        for (EntrypointContainer<InertiaMod> entrypoint : FabricLoader.getInstance().getEntrypointContainers("inertiabase", InertiaMod.class)) {
            InertiaMod mod = entrypoint.getEntrypoint();

            mod.setName(entrypoint.getProvider().getMetadata().getName());
            mod.setId(entrypoint.getProvider().getMetadata().getId());
            mods.add(mod);
        }
    }

}
