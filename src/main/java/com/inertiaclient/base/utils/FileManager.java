package com.inertiaclient.base.utils;

import com.google.gson.*;
import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl.ClientTickEvent;
import com.inertiaclient.base.hud.HudGroup;
import com.inertiaclient.base.mods.InertiaMod;
import com.inertiaclient.base.module.Module;
import lombok.*;

import java.io.File;
import java.util.ArrayList;

public class FileManager {


    @Getter
    private Gson normalGson = new GsonBuilder().disableHtmlEscaping().create();
    @Getter
    private Gson formattedGson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();


    private File mainDirectory;
    @Getter
    private File librariesDirectory;
    private File configsDirectory;
    @Getter
    private boolean mostLikelyFirstTimeLoading;

    @Getter
    private QueueSave moduleQueuedSave = new QueueSave(() -> {
        this.saveModulesJson();
        InertiaBase.LOGGER.info("saved module json");
    });

    @Getter
    private QueueSave hudQueuedSave = new QueueSave(() -> {
        InertiaBase.LOGGER.info("saved hud json");
        this.saveHudJson();
    });

    @EventTarget
    private final EventListener<ClientTickEvent> clientTickListener = this::onEvent;

    public FileManager(File gameDirectory) {
        //this.mainDirectory = new File(gameDirectory + File.separator + "Inertia", InertiaClient.MINECRAFT_VERSION);
        this.mainDirectory = new File(gameDirectory, "icb");
        if (!this.mainDirectory.exists()) {
            this.mainDirectory.mkdirs();
            this.mostLikelyFirstTimeLoading = true;
        }

        this.librariesDirectory = this.getGameFolder("Libraries");
        this.configsDirectory = this.getGameFolder("Configs");

        EventManager.register(this);
    }

    public File getGameFolder(String folderName) {
        File file = new File(this.mainDirectory, folderName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public File getSaveFolderForMod(File rootFolder, InertiaMod inertiaMod) {

        File modsFolder = new File(rootFolder, "Mods");
        if (!modsFolder.exists()) {
            modsFolder.mkdirs();
        }

        File file = new File(modsFolder, inertiaMod.getName());
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public void saveModulesJson() {
        this.saveModulesJson(this.mainDirectory);
    }

    private void saveModulesJson(File rootFolder) {
        try {

            for (InertiaMod mod : InertiaBase.instance.getModLoader().getMods()) {
                this.saveModsModulesJson(rootFolder, mod);
            }
        } catch (Exception e) {
        }
    }

    private void saveModsModulesJson(File rootFolder, InertiaMod mod) throws Exception {
        ArrayList<Module> modulesAddByMod = InertiaBase.instance.getModuleManager().getModulesByMods().get(mod);

        File file = new File(this.getSaveFolderForMod(rootFolder, mod), "Modules.json");
        if (!file.exists()) {
            file.createNewFile();
        }

        JsonObject main = new JsonObject();
        for (Module module : modulesAddByMod) {
            try {
                main.add(module.getId(), module.toJson());
            } catch (Exception e) {
                InertiaBase.LOGGER.warn("Failed to encode Module {} to json", module.getId());
                e.printStackTrace();
            }
        }
        FileUtils.writeToFile(file, this.formattedGson.toJson(main));
    }

    public void loadModulesJson() {
        this.loadModulesJson(this.mainDirectory);
    }

    private void loadModulesJson(File rootFolder) {
        try {

            for (InertiaMod mod : InertiaBase.instance.getModLoader().getMods()) {
                this.loadModsModulesJson(rootFolder, mod);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.saveModulesJson();
        }
    }

    private void loadModsModulesJson(File rootFolder, InertiaMod mod) throws Exception {
        File modFolder = this.getSaveFolderForMod(rootFolder, mod);
        if (!modFolder.exists()) {
            return;
        }

        File file = new File(modFolder, "Modules.json");
        if (!file.exists()) {
            this.saveModulesJson();
            return;
        }

        JsonObject jsonObject = FileUtils.getJsonObjectFromFile(file);
        jsonObject.entrySet().forEach(entry -> {

            Module module = InertiaBase.instance.getModuleManager().getModuleById(entry.getKey());
            if (module != null) {
                try {
                    module.fromJson(entry.getValue());
                } catch (Exception e) {
                    InertiaBase.LOGGER.warn("Failed to decode Module {} from json", module.getId());
                    e.printStackTrace();
                }
            }
        });
    }

    public void saveHudJson() {
        this.saveHudJson(this.mainDirectory);
    }

    private void saveHudJson(File rootFolder) {
        try {

            File file = new File(rootFolder, "Hud.json");
            if (!file.exists()) {
                file.createNewFile();
            }

            JsonArray main = new JsonArray();
            for (HudGroup group : InertiaBase.instance.getHudManager().getGroups()) {
                try {
                    main.add(group.toJson());
                } catch (Exception e) {
                    InertiaBase.LOGGER.warn("Failed to encode Hud Group {} to json", group);
                    e.printStackTrace();
                }
            }
            FileUtils.writeToFile(file, this.formattedGson.toJson(main));
        } catch (Exception e) {
        }
    }

    public void loadHudJson() {
        this.loadHudJson(this.mainDirectory);
    }

    private void loadHudJson(File rootFolder) {
        try {
            File file = new File(rootFolder, "Hud.json");
            if (!file.exists()) {
                this.saveHudJson();
            }

            JsonArray jsonArray = FileUtils.getJsonArrayFromFile(file);
            jsonArray.forEach(jsonElement -> {
                HudGroup hudGroup = new HudGroup();
                InertiaBase.instance.getHudManager().getGroups().add(hudGroup);
                try {
                    hudGroup.fromJson(jsonElement);
                } catch (Exception e) {
                    InertiaBase.LOGGER.warn("Failed to decode Hud Group {} from json", hudGroup);
                    e.printStackTrace();
                }
            });

            //InertiaClient.instance.getHudManager().getGroups().removeIf(hudGroup -> hudGroup.getElements().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            this.saveHudJson();
        }
    }

    public boolean saveConfig(String name, boolean module, boolean hud, boolean settings) {

        File configFolder = new File(this.configsDirectory, name);
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        try {
            if (module) {
                this.saveModulesJson(configFolder);
            }
            if (hud) {
                this.saveHudJson(configFolder);
            }
            InertiaBase.instance.getModLoader().getMods().forEach(inertiaMod -> {
                inertiaMod.onConfigSaved(configFolder);
            });

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean loadConfig(File configFolder) {
        try {
            this.loadModulesJson(configFolder);
            this.loadHudJson(configFolder);
            InertiaBase.instance.getModLoader().getMods().forEach(inertiaMod -> {
                inertiaMod.onConfigLoaded(configFolder);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public File[] getConfigs() {
        try {
            File file = new File(this.configsDirectory.getAbsolutePath());

            File[] folders = file.listFiles(File::isDirectory);

            return folders;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onEvent(ClientTickEvent event) {
        this.moduleQueuedSave.saveIfNecessary();
        this.hudQueuedSave.saveIfNecessary();
    }

    @RequiredArgsConstructor
    public class QueueSave {

        @Setter
        private boolean shouldSaveFile;
        private TimerUtil fileSaveTimer = new TimerUtil(true);
        @NonNull
        private Runnable saveFile;

        private void saveIfNecessary() {
            this.fileSaveTimer.update();
            //every 5 seconds check if we should save, then save
            if (this.fileSaveTimer.hasDelayRun(5 * 1000)) {
                this.fileSaveTimer.reset();
                if (this.shouldSaveFile) {
                    this.saveFile.run();
                }
                this.shouldSaveFile = false;
            }
        }

        public void queue() {
            this.shouldSaveFile = true;
        }


    }

}
