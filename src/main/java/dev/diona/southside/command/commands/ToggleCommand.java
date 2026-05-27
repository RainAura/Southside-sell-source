package dev.diona.southside.command.commands;

import dev.diona.southside.Southside;
import dev.diona.southside.command.Command;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.player.ChatUtil;

public class ToggleCommand extends Command {
    public ToggleCommand(String description) {
        super(description);
    }

    @Override
    public void run(String[] args) {
        if (args.length != 2) {
            this.printUsage();
            return;
        }
        Module module = Southside.moduleManager.getModuleByName(args[1]);
        if (module == null) {
            ChatUtil.sendText("Module not found!");
            return;
        }
        module.toggle();
        ChatUtil.sendText("Toggled " + module.getName() + " " + (module.isEnabled() ? "ON" : "OFF") + ".");
    }

    @Override
    public void printUsage() {
        ChatUtil.sendText("Usage: " + Southside.commandManager.PREFIX + "toggle <module>");
    }
}
