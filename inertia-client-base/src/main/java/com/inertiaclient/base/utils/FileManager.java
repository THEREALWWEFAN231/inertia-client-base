package com.inertiaclient.base.utils;

import com.google.gson.*;
import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl.ClientTickEvent;
import com.inertiaclient.base.hud.HudComponent;
import com.inertiaclient.base.hud.HudGroup;
import com.inertiaclient.base.mods.InertiaMod;
import com.inertiaclient.base.module.Module;
import lombok.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileManager {


    @Getter
    private Gson normalGson = new GsonBuilder().disableHtmlEscaping().create();
    @Getter
    private Gson formattedGson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();


    private Path mainDirectory;
    @Getter
    private Path librariesDirectory;
    private Path configsDirectory;
    @Getter
    private boolean mostLikelyFirstTimeLoading;

    @Getter
    private SkinCache skinCache;

    @Getter
    private QueueSave hudGroupQueuedSave = new QueueSave(() -> {
        InertiaBase.LOGGER.info("saved hud group json");
        this.saveHudGroupsJson();
    });

    @Getter
    private QueueSave settingsQueuedSave = new QueueSave(() -> {
        InertiaBase.LOGGER.info("saved ICB settings json");
        this.saveSettingsJson();
    });

    @EventTarget
    private final EventListener<ClientTickEvent> clientTickListener = this::onEvent;

    public FileManager(Path gameDirectory) {
        //this.mainDirectory = new File(gameDirectory + File.separator + "Inertia", InertiaClient.MINECRAFT_VERSION);
        this.mainDirectory = gameDirectory.resolve("icb");
        if (Files.notExists(mainDirectory)) {
            try {
                Files.createDirectories(mainDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.mostLikelyFirstTimeLoading = true;
        }

        this.librariesDirectory = this.getGameFolder("Libraries");
        this.configsDirectory = this.getGameFolder("Configs");

        Path skinHeadCache = this.getGameFolder("Skin Cache").resolve("Heads");
        if (Files.notExists(skinHeadCache)) {
            try {
                Files.createDirectories(skinHeadCache);
            } catch (IOException e) {
                InertiaBase.LOGGER.error("Failed to create Skin Cache Heads folder");
            }
        }
        this.skinCache = new SkinCache(skinHeadCache);

        EventManager.register(this);
    }

    public Path getGameFolder(String folderName) {
        Path path = this.mainDirectory.resolve(folderName);
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                InertiaBase.LOGGER.error("Failed to create game folder {}", folderName);
            }
        }
        return path;
    }

    public Path getSaveFolderForMod(Path rootFolder, InertiaMod inertiaMod) throws IOException {

        Path modsFolder = rootFolder.resolve("Mods");
        if (Files.notExists(modsFolder)) {
            Files.createDirectories(modsFolder);
        }

        Path path = modsFolder.resolve(inertiaMod.getName());
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    public void clientLoaded() {
        this.loadSettingsJson();
        this.loadAllModsModulesJson();
        this.loadAllModsHudComponentsJson();
        this.loadHudGroupsJson();
        this.loadFriendsJson();
    }

    public void saveSettingsJson() {
        try {
            Path file = this.mainDirectory.resolve("Settings.json");
            if (Files.notExists(file)) {
                Files.createFile(file);
            }

            JsonObject main = new JsonObject();
            main.add("Value Groups", InertiaBase.instance.getSettings().getValueGroups().toJson());
            FileUtils.writeToFile(file, this.formattedGson.toJson(main));
        } catch (Exception e) {
            InertiaBase.LOGGER.warn("Error saving ICB settings", e);
        }
    }

    public void loadSettingsJson() {
        try {
            Path file = this.mainDirectory.resolve("Settings.json");
            if (Files.notExists(file)) {
                this.saveSettingsJson();
                return;
            }

            JsonObject main = FileUtils.getJsonObjectFromFile(file);
            JsonElement groups = main.get("Value Groups");
            InertiaBase.instance.getSettings().getValueGroups().fromJson(groups);
        } catch (Exception e) {
            InertiaBase.LOGGER.warn("Failed to decode ICB settings json, saving instead", e);
            this.saveSettingsJson();
        }
    }

    public void saveModulesJson() {
        this.saveModulesJson(this.mainDirectory);
    }

    private void saveModulesJson(Path rootFolder) {
        for (InertiaMod mod : InertiaBase.instance.getModLoader().getMods()) {
            this.saveModsModulesJson(rootFolder, mod);
        }
    }

    private boolean saveModsModulesJson(Path rootFolder, InertiaMod mod) {
        try {
            ArrayList<Module> modulesAddedByMod = InertiaBase.instance.getModuleManager().getModulesByMods().get(mod);

            Path file = this.getSaveFolderForMod(rootFolder, mod).resolve("Modules.json");
            if (Files.notExists(file)) {
                Files.createFile(file);
            }

            JsonObject main = new JsonObject();
            for (Module module : modulesAddedByMod) {
                try {
                    main.add(module.getId(), module.toJson());
                } catch (Exception e) {
                    InertiaBase.LOGGER.warn("Failed to encode Module {} to json", module.getId(), e);
                }
            }
            FileUtils.writeToFile(file, this.formattedGson.toJson(main));
        } catch (Exception e) {
            InertiaBase.LOGGER.warn("Failed to save {} modules json", mod.getId(), e);
            return false;
        }
        return true;
    }

    public void loadAllModsModulesJson() {
        this.loadAllModsModulesJson(this.mainDirectory);
    }

    private void loadAllModsModulesJson(Path rootFolder) {
        try {

            for (InertiaMod mod : InertiaBase.instance.getModLoader().getMods()) {
                this.loadModulesJsonByMod(rootFolder, mod);
            }
        } catch (Exception e) {
            InertiaBase.LOGGER.warn("Failed to decode modules json, saving instead", e);
            this.saveModulesJson(rootFolder);
        }
    }

    private void loadModulesJsonByMod(Path rootFolder, InertiaMod mod) throws Exception {
        Path modFolder = this.getSaveFolderForMod(rootFolder, mod);
        if (Files.notExists(modFolder)) {
            return;
        }

        Path file = modFolder.resolve("Modules.json");
        if (Files.notExists(file)) {
            this.saveModsModulesJson(rootFolder, mod);
            return;
        }

        JsonObject jsonObject = FileUtils.getJsonObjectFromFile(file);
        jsonObject.entrySet().forEach(entry -> {

            Module module = InertiaBase.instance.getModuleManager().getModuleById(entry.getKey());
            if (module != null) {
                try {
                    module.fromJson(entry.getValue());
                } catch (Exception e) {
                    InertiaBase.LOGGER.warn("Failed to decode Module {} from json", module.getId(), e);
                }
            }
        });
    }

    public void saveHudGroupsJson() {
        this.saveHudGroupsJson(this.mainDirectory);
    }

    private void saveHudGroupsJson(Path rootFolder) {
        try {

            Path file = rootFolder.resolve("Hud Groups.json");
            if (Files.notExists(file)) {
                Files.createFile(file);
            }

            JsonArray main = new JsonArray();
            for (HudGroup group : InertiaBase.instance.getHudManager().getGroups()) {
                try {
                    main.add(group.toJson());
                } catch (Exception e) {
                    InertiaBase.LOGGER.warn("Failed to encode Hud Group {} to json", group, e);
                }
            }
            FileUtils.writeToFile(file, this.formattedGson.toJson(main));
        } catch (Exception e) {
        }
    }

    public void loadHudGroupsJson() {
        this.loadHudGroupsJson(this.mainDirectory);
    }

    private void loadHudGroupsJson(Path rootFolder) {
        try {
            Path file = rootFolder.resolve("Hud Groups.json");
            if (Files.notExists(file)) {
                this.saveHudGroupsJson();
            }

            JsonArray jsonArray = FileUtils.getJsonArrayFromFile(file);
            jsonArray.forEach(jsonElement -> {
                HudGroup hudGroup = new HudGroup();
                InertiaBase.instance.getHudManager().getGroups().add(hudGroup);
                try {
                    hudGroup.fromJson(jsonElement);
                } catch (Exception e) {
                    InertiaBase.LOGGER.warn("Failed to decode Hud Group {} from json", jsonElement, e);
                }
            });

            //InertiaClient.instance.getHudManager().getGroups().removeIf(hudGroup -> hudGroup.getElements().isEmpty());
        } catch (Exception e) {
            InertiaBase.LOGGER.warn("Failed to decode hud json, saving instead", e);
            this.saveHudGroupsJson();
        }
    }

    public void saveHudComponentsJson() {
        this.saveHudComponentsJson(this.mainDirectory);
    }

    private void saveHudComponentsJson(Path rootFolder) {
        for (InertiaMod mod : InertiaBase.instance.getModLoader().getMods()) {
            this.saveModsHudComponentJson(rootFolder, mod);
        }
    }

    private boolean saveModsHudComponentJson(Path rootFolder, InertiaMod mod) {
        try {
            ArrayList<HudComponent> componentsAddedByMod = InertiaBase.instance.getHudManager().getComponentsByMods().get(mod);

            Path file = this.getSaveFolderForMod(rootFolder, mod).resolve("Hud Components.json");
            if (Files.notExists(file)) {
                Files.createFile(file);
            }

            JsonObject main = new JsonObject();
            for (HudComponent hudComponent : componentsAddedByMod) {
                try {
                    main.add(hudComponent.getId(), hudComponent.toJson());
                } catch (Exception e) {
                    InertiaBase.LOGGER.warn("Failed to encode Hud Component {} to json", hudComponent.getId(), e);
                }
            }
            FileUtils.writeToFile(file, this.formattedGson.toJson(main));
        } catch (Exception e) {
            InertiaBase.LOGGER.warn("Failed to save hud component json", e);
            return false;
        }
        return true;
    }

    public void loadAllModsHudComponentsJson() {
        this.loadAllModsHudComponentsJson(this.mainDirectory);
    }

    private void loadAllModsHudComponentsJson(Path rootFolder) {
        try {

            for (InertiaMod mod : InertiaBase.instance.getModLoader().getMods()) {
                this.loadHudComponentsJsonByMod(rootFolder, mod);
            }
        } catch (Exception e) {
            InertiaBase.LOGGER.warn("Failed to decode hud components json, saving instead", e);
            this.saveHudComponentsJson(rootFolder);
        }
    }

    private void loadHudComponentsJsonByMod(Path rootFolder, InertiaMod mod) throws Exception {
        Path modFolder = this.getSaveFolderForMod(rootFolder, mod);
        if (Files.notExists(modFolder)) {
            return;
        }
        
        Path file = modFolder.resolve("Hud Components.json");
        if (Files.notExists(file)) {
            this.saveModsHudComponentJson(rootFolder, mod);
            return;
        }

        JsonObject jsonObject = FileUtils.getJsonObjectFromFile(file);
        jsonObject.entrySet().forEach(entry -> {

            HudComponent hudComponent = InertiaBase.instance.getHudManager().getIdCachedComponentsMap().get(entry.getKey());
            if (hudComponent != null) {
                try {
                    hudComponent.fromJson(entry.getValue());
                } catch (Exception e) {
                    InertiaBase.LOGGER.warn("Failed to decode Hud Component {} from json", hudComponent.getId(), e);
                }
            }
        });
    }

    public boolean saveConfig(String name, boolean module, boolean hud, boolean settings) {

        Path configFolder = configsDirectory.resolve(name);
        if (Files.notExists(configFolder)) {
            try {
                Files.createDirectories(configFolder);
            } catch (IOException e) {
                InertiaBase.LOGGER.error("Failed to create config directory", e);
            }
        }

        try {
            if (module) {
                this.saveModulesJson(configFolder);
            }
            if (hud) {
                this.saveHudGroupsJson(configFolder);
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

    public boolean loadConfig(Path configFolder) {
        try {
            this.loadAllModsModulesJson(configFolder);
            this.loadHudGroupsJson(configFolder);
            InertiaBase.instance.getModLoader().getMods().forEach(inertiaMod -> {
                inertiaMod.onConfigLoaded(configFolder);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void saveFriendsJson() {
        this.saveFriendsJson(this.mainDirectory);
    }

    private void saveFriendsJson(Path rootFolder) {
        try {

            Path file = rootFolder.resolve("Friends.json");
            if (Files.notExists(file)) {
                Files.createFile(file);
            }

            JsonArray main = new JsonArray();
            for (Friend friend : InertiaBase.instance.getFriendManager().getFriends()) {
                try {
                    main.add(friend.toJson());
                } catch (Exception e) {
                    InertiaBase.LOGGER.warn("Failed to encode friend {} to json", friend, e);
                }
            }
            FileUtils.writeToFile(file, this.formattedGson.toJson(main));
        } catch (Exception e) {
            InertiaBase.LOGGER.warn("Failed to save friends json", e);
        }
    }

    public void loadFriendsJson() {
        this.loadFriendsJson(this.mainDirectory);
    }

    private void loadFriendsJson(Path rootFolder) {
        try {
            Path file = rootFolder.resolve("Friends.json");
            if (Files.notExists(file)) {
                this.saveFriendsJson();
            }

            InertiaBase.instance.getFriendManager().removeAllFriends();

            JsonArray jsonArray = FileUtils.getJsonArrayFromFile(file);
            jsonArray.forEach(jsonElement -> {
                try {
                    Friend friend = Friend.makeFromJson(jsonElement);
                    InertiaBase.instance.getFriendManager().addFriend(friend, false);
                } catch (Exception e) {
                    InertiaBase.LOGGER.warn("Failed to decode friend {} from json", jsonElement, e);
                }
            });
        } catch (Exception e) {
            InertiaBase.LOGGER.warn("Failed to decode friends json, saving instead", e);
            this.saveFriendsJson();
        }
    }


    public ArrayList<Path> getConfigs() {
        ArrayList<Path> configs = new ArrayList<>();
        try (DirectoryStream<Path> configStream = Files.newDirectoryStream(this.configsDirectory)) {
            for (Path path : configStream) {
                if (Files.isDirectory(path)) {
                    configs.add(path);
                }
            }
        } catch (IOException e) {
            InertiaBase.LOGGER.error("Failed to get local config", e);
        }
        return configs;
    }

    public void onEvent(ClientTickEvent event) {
        this.hudGroupQueuedSave.saveIfNecessary();
        this.settingsQueuedSave.saveIfNecessary();

        InertiaBase.instance.getModLoader().getMods().forEach(inertiaMod -> {
            inertiaMod.getModSave().getModuleQueuedSave().saveIfNecessary();
            inertiaMod.getModSave().getHudComponentQueuedSave().saveIfNecessary();
        });
    }

    @RequiredArgsConstructor
    public static class QueueSave {

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

    public static class ModSave {

        @Getter
        private QueueSave moduleQueuedSave;

        @Getter
        private QueueSave hudComponentQueuedSave;

        public ModSave(InertiaMod mod) {
            this.moduleQueuedSave = new QueueSave(() -> {
                FileManager fileManager = InertiaBase.instance.getFileManager();
                if (fileManager.saveModsModulesJson(fileManager.mainDirectory, mod)) {
                    InertiaBase.LOGGER.info("saved {} modules json", mod.getId());
                }
            });

            this.hudComponentQueuedSave = new QueueSave(() -> {
                FileManager fileManager = InertiaBase.instance.getFileManager();
                if (fileManager.saveModsHudComponentJson(fileManager.mainDirectory, mod)) {
                    InertiaBase.LOGGER.info("saved {} hud components json", mod.getId());
                }
            });
        }
    }

}
