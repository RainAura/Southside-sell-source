package dev.diona.southside.managers;

import com.google.gson.JsonObject;
import dev.diona.southside.Southside;
import dev.diona.southside.command.Command;
import dev.diona.southside.command.commands.*;
import dev.diona.southside.event.events.SendMessageEvent;
import dev.diona.southside.util.player.ChatUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandManager {
    public static final String DEFAULT_PREFIX = ".";
    public final String PREFIX;

    private final HashMap<String, Command> commands = new HashMap<String, Command>();

    private final HashMap<String[], String> descriptions = new HashMap<String[], String>();

    public CommandManager() {
        this.PREFIX = this.getCommandPrefix();
        Southside.eventBus.subscribe(this);

        this.register(new String[]{"bind", "binds"}, new BindCommand("Set the binding of modules."));
        this.register(new String[]{"config"}, new ConfigCommand("Switch configurations."));
        this.register(new String[]{"help"}, new HelpCommand("Display help message."));
//        this.register(new String[]{"set"}, new SetCommand("Set modules' values."));
        this.register(new String[]{"t", "toggle"}, new ToggleCommand("Toggle a module."));
    }

    private String getCommandPrefix() {
        JsonObject clientInfo = Southside.fileManager.readFileData(FileManager.CLIENT_INFO).getAsJsonObject();
        if (clientInfo.get("commandPrefix") == null) {
            clientInfo.addProperty("commandPrefix", DEFAULT_PREFIX);
            Southside.fileManager.writeData(FileManager.CLIENT_INFO, clientInfo);
        }
        return clientInfo.get("commandPrefix").getAsString();
    }

    @EventListener
    public void onMessage(SendMessageEvent event) {
        String message = event.getMessage();
        if (!message.startsWith(PREFIX)) return;
        String[] args = message.substring(PREFIX.length()).split(" ");
        if (args.length == 0) {
            this.sendHelp();
        } else if (commands.containsKey(args[0])) {
            Command cmd = commands.get(args[0]);
            cmd.run(args);
        } else {
            this.sendHelp();
        }
        event.setCancelled(true);
    }

    private void register(String[] prefixes, Command command) {
        descriptions.put(prefixes, command.description);
        for (String prefix : prefixes) {
            commands.put(prefix, command);
        }
    }

    private TextComponentString buildHelpComponent() {
        TextComponentString prefix = new TextComponentString("Invalid command, try ");
        TextComponentString help = new TextComponentString("\".help\"");
        TextComponentString suffix = new TextComponentString(".");
        help.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString( "Click to try")));
        help.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ".help"));
        return (TextComponentString) prefix.appendSibling(help).appendSibling(suffix);
    }

    private void sendHelp() {
        ChatUtil.sendComponent(this.buildHelpComponent());
    }

    public TextComponentString getHelp() {
        ArrayList<String> helpMessages = new ArrayList<>();
        descriptions.entrySet().forEach((e) -> {
            helpMessages.add(String.join(", ", e.getKey()) + ": " + e.getValue());
        });
        return new TextComponentString(String.join("\n", helpMessages));
    }
}
