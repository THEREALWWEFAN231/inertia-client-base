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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.Cleaner;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Function;

public class InertiaBase {

    public static final String CLIENT_NAME = "ICB";
    public static final String VERSION;
    public static final String MINECRAFT_VERSION = "1.21.4";//basically equal to SharedConstants.getGameVersion().getName()?
    public static final Minecraft mc = Minecraft.getInstance();
    public static final InertiaBase instance = new InertiaBase();
    public static final Logger LOGGER = LoggerFactory.getLogger("icb");
    public static final String WEBSITE = "https://inertiaclient.com";
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(7500)).build();
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
        mc.gui.hud.getChat().addClientSystemMessage(createChatMessage(message));
    }

    public static void sendFileChatMessage(Object message, String file) {
        MutableComponent chatMessage = createChatMessage(message);

        chatMessage.setStyle(Style.EMPTY.withClickEvent(new ClickEvent.OpenFile(file)).withHoverEvent(new HoverEvent.ShowText(Component.literal(file))));
    }


    /**
     *
     * @param urlString      url to go to
     * @param bodyHandler    how to accept the body as an object, HttpResponse.BodyHandlers
     * @param responseAction what to do with the response from the website, parsed as an object from bodyHandler
     * @param <R>            any object
     * @return can be {@link org.jetbrains.annotations.Nullable}
     */
    @Nullable
    public static <T, R> R createWebRequest(String urlString, HttpResponse.BodyHandler<T> bodyHandler, Function<HttpResponse<T>, R> responseAction) {
        URI url = null;
        try {
            url = new URI(urlString);
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to parse url \"{}\"", urlString, e);
            return null;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(url).header("User-Agent", InertiaBase.getUserAgentForURL(url)).GET().build();

            HttpResponse<T> response = HTTP_CLIENT.send(request, bodyHandler);
            try {
                return responseAction.apply(response);
            } catch (Exception e) {
                LOGGER.error("Error during response action to url \"{}\"", urlString, e);
            }

        } catch (Exception e) {
            LOGGER.error("Failed a web request to url \"{}\"", urlString, e);
        }
        return null;
    }

    public static String getUserAgentForURL(URI uri) {
        return "icb Version " + InertiaBase.VERSION + " " + uri.getHost() + uri.getPath();
    }

    static {
        var icbFabricMod = FabricLoader.getInstance().getModContainer("icb");
        if (icbFabricMod.isPresent()) {
            VERSION = icbFabricMod.get().getMetadata().getVersion().getFriendlyString();
        } else {
            VERSION = "unknown";
            LOGGER.warn("Failed to get ICB version from fabric.mod.json, this shouldn't happen");
        }
    }

}
