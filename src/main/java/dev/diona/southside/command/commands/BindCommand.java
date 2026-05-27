package dev.diona.southside.command.commands;

import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import dev.diona.southside.Southside;
import dev.diona.southside.command.Command;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.player.ChatUtil;
import org.lwjglx.input.Keyboard;

public class BindCommand extends Command {
    public BindCommand(String description) {
        super(description);
    }

    @Override
    public void run(String[] args) {
        if (args[0].equals("binds")) {
            if (args.length != 1) {
                this.printUsage();
                return;
            }
            StringBuilder message = new StringBuilder("Bind list:");
            for (Module module : Southside.moduleManager.getModules()) {
                if (!module.keyBind.value.getKeyBinds().isEmpty() && !module.keyBind.value.getDisplay().equals("NONE")) {
                    message.append("\n").append(module.getName()).append(": ").append(module.keyBind.value.getDisplay());
                }
            }
            ChatUtil.sendText(message.toString());
            return;
        }
        if (args.length != 3) {
            this.printUsage();
            return;
        }
        Module module = Southside.moduleManager.getModuleByName(args[1]);
        if (module == null) {
            ChatUtil.sendText("Module not found!");
            return;
        }
        int keyIndex = Keyboard.getKeyIndex(args[2].toUpperCase());
        module.keyBind.getValue().clearKeys();
        module.keyBind.getValue().addKey(keyIndex);
        ChatUtil.sendText(module.getName() + " has been bound to " + Keyboard.getKeyName(keyIndex) + ".");
    }

    @Override
    public void printUsage() {
        ChatUtil.sendText("Usage: " + Southside.commandManager.PREFIX + "bind <module> <key>");
    }
}
