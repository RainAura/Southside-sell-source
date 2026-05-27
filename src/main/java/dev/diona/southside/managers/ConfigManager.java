//package dev.diona.southside.managers;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import dev.diona.southside.Southside;
//import dev.diona.southside.gui.click.ClickGuiScreen;
//import dev.diona.southside.module.Serializable;
//import dev.diona.southside.module.modules.client.ClickGui;
//import dev.diona.southside.util.player.ChatUtil;
//import org.lwjglx.Sys;
//
//import java.io.File;
//import java.io.IOException;
//
//import static dev.diona.southside.Southside.MC.mc;
//
//public class ConfigManager implements Serializable {
//    public static final File CONFIG_DIRECTORY = new File(FileManager.CLIENT_DIRECTORY, "config");
//    public static final String DEFAULT_CONFIG = "default";
//
//    public String loadedConfig;
//    public ConfigManager() {
//        if (!CONFIG_DIRECTORY.exists()) {
//            if (!CONFIG_DIRECTORY.mkdir()) {
//                Southside.LOGGER.error("Failed to create the config file folder!");
//            }
//        }
////        this.loadedConfig = this.getLoadedConfigName();
//        loadConfig(getLoadedConfigName());
//    }
//
//    public String getLoadedConfigName() {
//        JsonObject clientInfo = Southside.fileManager.readFileData(FileManager.CLIENT_INFO).getAsJsonObject();
//        if (clientInfo.get("config") == null) {
//            clientInfo.addProperty("config", DEFAULT_CONFIG);
//            Southside.fileManager.writeData(FileManager.CLIENT_INFO, clientInfo);
//        }
//        return clientInfo.get("config").getAsString();
//    }
//
//    public void saveLoadedConfigName() {
//        JsonObject clientInfo = Southside.fileManager.readFileData(FileManager.CLIENT_INFO).getAsJsonObject();
//        clientInfo.addProperty("config", this.loadedConfig);
//        Southside.fileManager.writeData(FileManager.CLIENT_INFO, clientInfo);
//    }
//
//    public boolean loadConfig(String config) {
//        Southside.LOGGER.info("Loading config: " + config);
//        File configFile = new File(CONFIG_DIRECTORY, config + ".json");
//        if (!configFile.exists()) {
//            if (mc.player != null) {
//                ChatUtil.info("Config not exists: " + config);
//            }
//            loadDefaultConfig();
//            return false;
//        }
//        this.loadedConfig = config;
//        try {
//            this.deserialize(Southside.fileManager.readFileData(configFile));
//            Southside.fileManager.writeData(configFile, this.serialize());
//            this.saveConfig();
//            return true;
//        } catch (NullPointerException | IllegalStateException e) {
//            if (mc.player != null) {
//                ChatUtil.info("Failed to load config: " + config);
//            }
//            loadDefaultConfig();
//            return false;
//        }
//    }
//
//    public void loadDefaultConfig() {
//        this.loadedConfig = DEFAULT_CONFIG;
//        Southside.LOGGER.info("Loading config: " + DEFAULT_CONFIG);
//        File configFile = new File(CONFIG_DIRECTORY, DEFAULT_CONFIG + ".json");
//        if (!configFile.exists()) {
//            try {
//                assert configFile.createNewFile();
//                Southside.LOGGER.info("Missing default config, creating a new one!");
//            } catch (IOException | AssertionError e) {
//                throw new RuntimeException(e);
//            }
//        }
//        this.deserialize(Southside.fileManager.readFileData(configFile));
//        this.saveConfig();
//    }
//
//    public void saveConfig() {
//        if (mc.currentScreen == ClickGui.clickGuiScreen) return;
//        this.saveConfigWithoutClickGUICheck();
//    }
//
//    public void saveConfigWithoutClickGUICheck() {
//        this.saveConfig(this.loadedConfig);
//    }
//
//    public void saveConfig(String name) {
//        File configFile = new File(CONFIG_DIRECTORY, name + ".json");
//        Southside.fileManager.writeData(configFile, this.serialize());
//        this.saveLoadedConfigName();
//    }
//
//    @Override
//    public JsonElement serialize() {
//        JsonObject configObject = new JsonObject();
//        Southside.moduleManager.getModules().forEach(module -> {
//            configObject.add(module.getName(), module.serialize());
//        });
//        return configObject;
//    }
//
//    @Override
//    public void deserialize(JsonElement element) {
//        try {
//            Southside.moduleManager.getModules().forEach(module -> {
//                module.deserialize(element.getAsJsonObject().get(module.getName()));
//            });
//        } catch (NullPointerException | UnsupportedOperationException | IllegalStateException e) {
//            Southside.moduleManager.getModules().forEach(module -> {
//                module.deserialize(null);
//            });
//        }
//    }
//}
