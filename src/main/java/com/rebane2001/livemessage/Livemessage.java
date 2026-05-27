package com.rebane2001.livemessage;

import com.rebane2001.livemessage.gui.*;
import net.minecraft.client.Minecraft;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Livemessage {
    public static final String MOD_ID = "Southchat";
    public static Path modFolder;

    public static Livemessage instance = new Livemessage();
    public void init()
    {
        initDirs();
        LivemessageGui.loadBuddies();
    }

    /**
     * Creates relevant folders if they don't exists.
     */
    private void initDirs(){
        modFolder = Minecraft.getMinecraft().gameDir.toPath().resolve("config/livemessage");
        File directory = new File(String.valueOf(modFolder));
        if (!directory.exists())
            directory.mkdir();
        directory = new File(String.valueOf(modFolder.resolve("messages")));
        if (!directory.exists())
            directory.mkdir();
        directory = new File(String.valueOf(modFolder.resolve("settings")));
        if (!directory.exists())
            directory.mkdir();
        directory = new File(String.valueOf(modFolder.resolve("patterns")));
        if (!directory.exists())
            directory.mkdir();
    }
}
