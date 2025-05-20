package com.inertiaclient.base.module;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.mods.InertiaMod;
import com.inertiaclient.base.utils.CollectionUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

public class ModuleManager {

    @Getter
    private final ArrayList<Module> modules = new ArrayList<>();
    private final HashMap<Class<?>, Module> classModulesMap = new HashMap<>();
    private final HashMap<String, Module> idCachedModulesMap = new HashMap<>();
    @Getter
    private final HashMap<Category, ArrayList<Module>> modulesByCategory = new HashMap<>();
    @Getter
    private final HashMap<InertiaMod, ArrayList<Module>> modulesByMods = new HashMap<>();

    public ModuleManager() {
        InertiaBase.instance.getModLoader().getMods().forEach(inertiaMod -> {
            ArrayList<Module> modsModules = new ArrayList<>();
            inertiaMod.initializeModules(modsModules, this);

            modsModules.forEach(module -> this.registerModule(module, inertiaMod));
        });
    }

    private void registerModule(Module module, InertiaMod inertiaMod) {
        module.setMod(inertiaMod);

        if (idCachedModulesMap.containsKey(module.getId())) {
            Module alreadyRegistered = idCachedModulesMap.get(module.getId());
            module.setId(inertiaMod.getId() + "_" + module.getId());

            InertiaBase.LOGGER.warn("Module with id {} was already registered by {}, prefixing one of them with their mod id", alreadyRegistered.getId(), alreadyRegistered.getMod().getName());
        }

        this.modules.add(module);
        this.classModulesMap.put(module.getClass(), module);
        this.idCachedModulesMap.put(module.getId(), module);
        CollectionUtils.addToArrayListHashMap(this.modulesByCategory, module.getCategory(), module);
        CollectionUtils.addToArrayListHashMap(this.modulesByMods, inertiaMod, module);
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) this.classModulesMap.get(clazz);
    }

    public <T extends Module> T getModuleById(String moduleId) {
        return (T) this.idCachedModulesMap.get(moduleId);
    }

}

