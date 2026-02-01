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
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.Cleaner;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;

public class InertiaBase {

    public static final String CLIENT_NAME = "Inertia";
    public static final String VERSION = "0.0.1";
    public static final String MINECRAFT_VERSION = "1.21.4";//basically equal to SharedConstants.getGameVersion().getName()?
    public static final Minecraft mc = Minecraft.getInstance();
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

        this.fileManager = new FileManager(mc.gameDirectory.toPath());
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
        this.fileManager.loadFriendsJson();

        this.modLoader.getMods().forEach(InertiaMod::initialize);
    }

    public boolean isUpdateAvailable() {
        return !this.mostRecentVersion.equals(VERSION);
    }

    private static MutableComponent createChatMessage(Object message) {
        CommandManager commandManager = instance.getCommandManager();

        MutableComponent text = Component.literal("");
        text.append(Component.literal("[").setStyle(Style.EMPTY.withColor(commandManager.getBracketColor())));
        text.append(Component.literal(CLIENT_NAME).setStyle(Style.EMPTY.withBold(true).withColor(commandManager.getNameColor())));
        text.append(Component.literal("]").setStyle(Style.EMPTY.withColor(commandManager.getBracketColor())));
        text.append(Component.literal(" "));

        if (message instanceof Component messageText) {
            if (message instanceof MutableComponent mutableText) {
                text.append(mutableText.setStyle(Style.EMPTY.withColor(commandManager.getMessageColor())));
            } else {
                text.append(messageText);
            }
        } else {
            text.append(Component.literal(message.toString()).setStyle(Style.EMPTY.withColor(commandManager.getMessageColor())));
        }
        return text;
    }

    public static void sendChatMessage(Object message) {
        mc.player.displayClientMessage(createChatMessage(message), false);
    }

    public static void sendFileChatMessage(Object message, String file) {
        MutableComponent chatMessage = createChatMessage(message);
        chatMessage.setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file)).withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal(file))));
        mc.player.displayClientMessage(chatMessage, false);
    }


    /**
     *
     * @param urlString      url to go to
     * @param responseAction what to do with the response from the website, usually {@link org.apache.http.util.EntityUtils}.something
     * @param <R>            any object
     * @return can be {@link org.jetbrains.annotations.Nullable}
     */
    @Nullable
    public static <R> R createWebRequest(String urlString, Function<CloseableHttpResponse, R> responseAction) {
        URI url = null;
        try {
            url = new URI(urlString);
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to parse url \"{}\"", urlString, e);
            return null;
        }
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(7500).build();
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()) {
            HttpGet getRequest = new HttpGet(url);

            getRequest.setHeader("User-Agent", InertiaBase.getUserAgentForURL(url));

            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                return responseAction.apply(response);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to web request to url \"{}\"", urlString, e);
        }
        return null;
    }

    public static String getUserAgentForURL(URI uri) {
        return "icb Version " + InertiaBase.VERSION + " " + uri.getHost() + uri.getPath();
    }
}
