package com.inertiaclient.base;

import com.inertiaclient.base.command.CommandManager;
import com.inertiaclient.base.friend.FriendManager;
import com.inertiaclient.base.hud.HudManager;
import com.inertiaclient.base.mods.InertiaMod;
import com.inertiaclient.base.mods.ModLoader;
import com.inertiaclient.base.module.ModuleManager;
import com.inertiaclient.base.utils.FileManager;
import com.inertiaclient.base.utils.LibraryDownloader;
import com.inertiaclient.base.utils.TickRateCalculator;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.Cleaner;
import java.net.URI;

public class InertiaBase {

    public static final String CLIENT_NAME = "Inertia";
    public static final String VERSION = "0.0.1";
    public static final String MINECRAFT_VERSION = "1.21.4";//basically equal to SharedConstants.getGameVersion().getName()?
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final InertiaBase instance = new InertiaBase();
    public static final Logger LOGGER = LoggerFactory.getLogger("icb");
    public static final String WEBSITE = "https://inertiaclient.com";
    public static final Cleaner CLEANER = Cleaner.create();

    @Getter
    private ModLoader modLoader;
    @Getter
    private FileManager fileManager;
    @Getter
    private Settings settings;
    @Getter
    private ModuleManager moduleManager;
    @Getter
    private CommandManager commandManager;
    @Getter
    private HudManager hudManager;
    @Getter
    private TickRateCalculator tickRateCalculator;
    @Getter
    private FriendManager friendManager;

    @Getter
    @Setter
    private float timer = 1;

    @Getter
    private String mostRecentVersion = VERSION;

    public void initialize() {
        this.modLoader = new ModLoader();

        this.fileManager = new FileManager(mc.runDirectory);
        try {
            new LibraryDownloader().main();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        this.settings = new Settings();
        this.moduleManager = new ModuleManager();
        this.commandManager = new CommandManager();
        this.hudManager = new HudManager();
        this.tickRateCalculator = new TickRateCalculator();
        this.friendManager = new FriendManager();

        this.fileManager.loadModulesJson();
        this.fileManager.loadHudJson();

        this.modLoader.getMods().forEach(InertiaMod::initialize);
    }

    public boolean isUpdateAvailable() {
        return !this.mostRecentVersion.equals(VERSION);
    }

    private static MutableText createChatMessage(Object message) {
        CommandManager commandManager = instance.getCommandManager();

        MutableText text = Text.literal("");
        text.append(Text.literal("[").setStyle(Style.EMPTY.withColor(commandManager.getBracketColor())));
        text.append(Text.literal(CLIENT_NAME).setStyle(Style.EMPTY.withBold(true).withColor(commandManager.getNameColor())));
        text.append(Text.literal("]").setStyle(Style.EMPTY.withColor(commandManager.getBracketColor())));
        text.append(Text.literal(" "));

        if (message instanceof Text messageText) {
            if (message instanceof MutableText mutableText) {
                text.append(mutableText.setStyle(Style.EMPTY.withColor(commandManager.getMessageColor())));
            } else {
                text.append(messageText);
            }
        } else {
            text.append(Text.literal(message.toString()).setStyle(Style.EMPTY.withColor(commandManager.getMessageColor())));
        }
        return text;
    }

    public static void sendChatMessage(Object message) {
        mc.player.sendMessage(createChatMessage(message), false);
    }

    public static void sendFileChatMessage(Object message, String file) {
        MutableText chatMessage = createChatMessage(message);
        chatMessage.setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(file))));
        mc.player.sendMessage(chatMessage, false);
    }


    public static String getUserAgentForURL(URI uri) {
        return "icb Version " + InertiaBase.VERSION + " " + uri.getHost() + uri.getPath();
    }
}
