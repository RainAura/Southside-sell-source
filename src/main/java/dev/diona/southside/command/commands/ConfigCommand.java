package dev.diona.southside.command.commands;

import cc.polyfrost.oneconfig.internal.config.OneConfigConfig;
import cc.polyfrost.oneconfig.internal.config.core.ConfigCore;
import cc.polyfrost.oneconfig.internal.config.core.KeyBindHandler;
import cc.polyfrost.oneconfig.internal.config.profiles.Profiles;
import cc.polyfrost.oneconfig.libs.universal.ChatColor;
import cc.polyfrost.oneconfig.libs.universal.UChat;
import dev.diona.southside.Southside;
import dev.diona.southside.command.Command;
import dev.diona.southside.util.player.ChatUtil;
import net.minecraft.block.SoundType;

public class ConfigCommand extends Command {
    public ConfigCommand(String description) {
        super(description);
    }

    @Override
    public void run(String[] args) {
        if (args.length == 1) {
            this.printUsage();
            return;
        }
        switch (args[1]) {
            case "load" -> {
                if (args.length != 3) {
                    this.printUsage();
                    return;
                }
                String name = args[2];
                if (!Profiles.doesProfileExist(name)) {
                    UChat.chat(ChatColor.RED.getChar() + "The Profile \"" + name + "\" does not exist!");
                } else {
                    Profiles.loadProfile(name);
                    UChat.chat(ChatColor.GREEN.getChar() + "Switched to the \"" + name + "\" Profile.");
                }
            }
            case "rename" -> {
                if (args.length != 4) {
                    this.printUsage();
                    return;
                }
                String profile = args[2];
                String newName = args[3];
                if (!Profiles.doesProfileExist(profile)) {
                    UChat.chat(ChatColor.RED.getChar() + "The Profile \"" + profile + "\" does not exist!");
                } else {
                    Profiles.renameProfile(profile, newName);
                    UChat.chat(ChatColor.GREEN.getChar() + "Renamed the \"" + profile + "\" Profile to \" " + newName + "\".");
                }
            }
            case "remove" -> {
                if (args.length != 3) {
                    this.printUsage();
                    return;
                }
                String name = args[2];
                if (!Profiles.doesProfileExist(name)) {
                    UChat.chat(ChatColor.RED.getChar() + "The Profile \"" + name + "\" does not exist!");
                } else {
                    Profiles.deleteProfile(name);
                    UChat.chat(ChatColor.GREEN.getChar() + "Deleted the \"" + name + "\" Profile.");
                }
            }
            case "create" -> {
                String name = args[2];
                if (Profiles.doesProfileExist(name)) {
                    UChat.chat(ChatColor.RED.getChar() + "The Profile \"" + name + "\" already exists!");
                } else {
                    Profiles.createProfile(name);
                    if (Profiles.doesProfileExist(name)) Profiles.loadProfile(name);
                    ConfigCore.reset();
                    ConfigCore.saveAll();
                    UChat.chat(ChatColor.GREEN.getChar() + "Created the \"" + name + "\" Profile.");
                    KeyBindHandler.INSTANCE.reInitKeyBinds();
//                    if (Profiles.doesProfileExist(name)) Profiles.loadProfile(name);
//                    ConfigCore.reset();
//                    ConfigCore.saveAll();
                }
            }
            case "copy" -> {
                String name = args[2];
                if (Profiles.doesProfileExist(name)) {
                    UChat.chat(ChatColor.RED.getChar() + "The Profile \"" + name + "\" already exists!");
                } else {
                    Profiles.createProfile(name);
                    if (Profiles.doesProfileExist(name)) Profiles.loadProfile(name);
                    UChat.chat(ChatColor.GREEN.getChar() + "Created the \"" + name + "\" Profile.");
                    KeyBindHandler.INSTANCE.reInitKeyBinds();
                }
            }
            case "list" -> {
                if (args.length != 2) {
                    this.printUsage();
                    return;
                }
                StringBuilder builder = new StringBuilder()
                        .append(ChatColor.GOLD.getChar()).append("Available profiles:");
                for (String profile : Profiles.getProfiles()) {
                    builder.append("\n");
                    if (OneConfigConfig.currentProfile.getValue().equals(profile)) builder.append(ChatColor.GREEN.getChar());
                    else builder.append(ChatColor.RED.getChar());
                    builder.append(profile);
                }
                UChat.chat(builder.toString());
            }
            default -> {
                this.printUsage();
                return;
            }
        }
    }

    @Override
    public void printUsage() {
        ChatUtil.sendText("Usage:\n"
                + Southside.commandManager.PREFIX + "config load <name>\n"
                + Southside.commandManager.PREFIX + "config rename <name> <newname>\n"
                + Southside.commandManager.PREFIX + "config remove <name>\n"
                + Southside.commandManager.PREFIX + "config create <name>\n"
                + Southside.commandManager.PREFIX + "config copy <name>\n"
                + Southside.commandManager.PREFIX + "config list"
        );
    }
}
