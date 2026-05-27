package dev.diona.southside.command.commands;

import dev.diona.southside.Southside;
import dev.diona.southside.command.Command;
import dev.diona.southside.managers.CommandManager;
import dev.diona.southside.util.player.ChatUtil;

public class HelpCommand extends Command {
    public HelpCommand(String description) {
        super(description);
    }

    @Override
    public void run(String[] args) {
        if (args.length != 1) {
            this.printUsage();
            return;
        }
        ChatUtil.sendComponent(Southside.commandManager.getHelp());
    }

    @Override
    public void printUsage() {
        ChatUtil.sendText("Usage: " + Southside.commandManager.PREFIX + "help");
    }
}
