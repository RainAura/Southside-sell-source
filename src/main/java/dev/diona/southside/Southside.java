package dev.diona.southside;

import com.rebane2001.livemessage.Livemessage;
import dev.diona.southside.managers.*;
import dev.diona.southside.util.player.RotationUtil;
import dev.diona.southside.util.render.glyph.GlyphFontManager;
import me.bush.eventbus.bus.EventBus;
import me.sb.TestConfig;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjglx.Sys;
import org.lwjglx.opengl.Display;

public class Southside {
    public static final String CLIENT_NAME = "Southside";
    public static final String CLIENT_VERSION = "b13";
    public static final ClientType type = ClientType.RELEASE;
    // 本地测试的时候改成 DEV，同时不要把这个更改推送

    public static Logger LOGGER = LogManager.getLogger(Southside.class);


    public static EventBus eventBus = new EventBus();

    public static ModuleManager moduleManager;
    //    public static LegacyFontManager legacyFontManager;
    public static FontManager fontManager;
    public static RenderManager renderManager;
    public static FileManager fileManager;
//    public static ConfigManager configManager;
    public static StyleManager styleManager;
    public static CommandManager commandManager;
//    public static QuickMacroAltManager quickMacroAltManager;

    public static void start() {
        LOGGER.info("Starting Southside...");
        GlyphFontManager.INSTANCE.initialize();
//        new TestConfig();
        // Register events
        RotationUtil.initialize();

        // Initialize managers
        renderManager = new RenderManager();
        fontManager = new FontManager();
        moduleManager = new ModuleManager(); // this depends on Font Manager's loaded font sizes

        // File related managers
        fileManager = new FileManager();
//        configManager = new ConfigManager();
//        styleManager = new StyleManager();
        commandManager = new CommandManager();
//        quickMacroAltManager = new QuickMacroAltManager();
        Livemessage.instance.init();
        Display.setTitle(Southside.CLIENT_NAME + " Premium  " + Southside.CLIENT_VERSION + " - Minecraft 1.12.2 | " + type.name());
    }

    public static void stop() {
        LOGGER.info("Stopping Southside...");
//        configManager.saveConfig();
    }

    public interface MC {
        Minecraft mc = Minecraft.getMinecraft();
    }

    public enum ClientType {
        RELEASE,
        BETA,
        DEV;
    }
}
