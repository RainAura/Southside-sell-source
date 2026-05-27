package com.rebane2001.livemessage.gui;

import com.google.gson.Gson;
import com.rebane2001.livemessage.Livemessage;
import com.rebane2001.livemessage.LivemessageConfig;
import com.rebane2001.livemessage.util.LiveProfileCache;
import com.rebane2001.livemessage.util.LivemessageUtil;
import dev.diona.southside.util.chat.Chat;
import dev.diona.southside.util.chat.ChatChannel;
import dev.diona.southside.util.chat.ChatMessage;
import dev.diona.southside.util.chat.Member;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.opengl.GL11;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class LivemessageGui extends GuiScreen {

    public LivemessageGui() {
    }

    public static boolean buddiesLoaded = false;
    public static List<String> channels = new ArrayList<>();
    public static Map<String, Integer> unreadMessages = new HashMap<>();

    public static List<LiveWindow> liveWindows = new ArrayList<>();

    public static double sclOrig = 1;
    public static double scl = 1;
    public static int screenHeight = 1;
    public static int screenWidth = 1;

    // For handling buttons from LiveWindows
    // Unused right now
    public static void handleBtn(int action) {
        switch (action) {
            case 0:
                liveWindows.get(liveWindows.size() - 1).deactivateWindow();
                liveWindows.add(new LiveWindow());
                break;
        }
    }

    /**
     * Loads users into respective lists.
     */
    public static void loadBuddies() {
        channels.clear();
        for (ChatChannel channel : Chat.getInstance().getChannels()) {
            channels.add(channel.getName());
        }
        buddiesLoaded = true;
        // Sort lists for consistency (even tho by UUID)
        Collections.sort(channels);
    }

    /**
     * Sets the primary color of the window.
     */
    public static class BuddySettings {
        long lastMessage;
    }

    public static void markAllAsRead() {
        unreadMessages.clear();
    }

    public void setScl() {
        final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        scl = scaledresolution.getScaleFactor() / (float)LivemessageConfig.guiSettings.guiScale;

        screenHeight = (int) (scaledresolution.getScaledHeight_double() * scl);
        screenWidth = (int) (scaledresolution.getScaledWidth_double() * scl);
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.buttonList.clear();

        setScl();

        loadBuddies();

        if (liveWindows.isEmpty())
            liveWindows.add(new ManeWindow());
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    // Use this to open a new window or highlight existing one
    public static void openChatWindow(final String name) {
        if (name == null)
            return;
        liveWindows.get(liveWindows.size() - 1).deactivateWindow();
        // Look for existing window
        for (LiveWindow liveWindow : LivemessageGui.liveWindows) {
            if (!(liveWindow instanceof ChatWindow chatWindow)) {
                continue;
            }
            if (chatWindow.liveProfile.equals(name)) {
                chatWindow.activateWindow();
                LivemessageGui.liveWindows.removeIf(it -> it == chatWindow);
                LivemessageGui.liveWindows.add(chatWindow);
                return;
            }
        }
        // If existing window didn't exist, add a new one
        LivemessageGui.addChatWindow(new ChatWindow(name));
    }

    // Safe chatwindow adding
    private static void addChatWindow(ChatWindow chatWindow) {
        if (chatWindow.valid) {
            liveWindows.add(chatWindow);
        } else {
            liveWindows.get(liveWindows.size() - 1).activateWindow();
        }
    }

    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                //Do button stuff
                break;
        }
    }

    public void handleMouseInput() throws IOException {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        liveWindows.get(liveWindows.size() - 1).mouseMove((int) (mouseX * scl), (int) (mouseY * scl));
        int mWheelState = Mouse.getEventDWheel();
        if (mWheelState != 0)
            liveWindows.get(liveWindows.size() - 1).mouseWheel(mWheelState);

        super.handleMouseInput();
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (int i = liveWindows.size() - 1; i >= 0; i--) {
            LiveWindow liveWindow = liveWindows.get(i);
            if (liveWindow.mouseInWindow((int) (mouseX * scl), (int) (mouseY * scl))) {
                if (i != liveWindows.size() - 1) {
                    liveWindows.get(liveWindows.size() - 1).deactivateWindow();
                    liveWindow.activateWindow();
                    liveWindows.remove(i);
                    liveWindows.add(liveWindow);
                }
                break;
            }
        }
        liveWindows.get(liveWindows.size() - 1).mouseClicked((int) (mouseX * scl), (int) (mouseY * scl), mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        liveWindows.get(liveWindows.size() - 1).mouseReleased((int) (mouseX * scl), (int) (mouseY * scl), state);
        super.mouseReleased(mouseX, mouseY, state);
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        liveWindows.get(liveWindows.size() - 1).mouseClickMove((int) (mouseX * scl), (int) (mouseY * scl), clickedMouseButton, timeSinceLastClick);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        liveWindows.get(liveWindows.size() - 1).keyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    public static boolean newMessage(final String channelName, final ChatMessage message, boolean sentByMe) {
        boolean doHide = false;
        // Write message to chat history jsonl
        final var member = Chat.getInstance().getOnlineMembers().get(message.getUsername());
        // Increase unread messages counter
        unreadMessages.put(channelName, unreadMessages.getOrDefault(channelName, 0) + 1);
        //LiveHud.addToast("Message from " + channelName, message.getMessage());
        //LiveHud.playNotificationSound();
        final var format = String.format("§5[§r§7Chat§5]§r §l<%s>§r %s", message.getUsername(), message.getMessage());
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(format));
        // Send message to open window
        for (LiveWindow liveWindow : liveWindows) {
            if (!(liveWindow instanceof ChatWindow))
                continue;
            ChatWindow chatWindow = (ChatWindow) liveWindow;
            if (channelName.equals(chatWindow.liveProfile)) {
                chatWindow.chatHistory.add(new ChatWindow.ChatMessage(member, message.getMessage(), sentByMe, message.getTimestamp()));
                if (chatWindow.chatScrolledToBottom)
                    chatWindow.chatScrollPosition += 1;
                break;
            }
        }
        return doHide;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        setScl();
        float reverseGuiScale = (float) (1f / scl * 1);
        GlStateManager.pushMatrix();
        GlStateManager.scale(reverseGuiScale, reverseGuiScale, reverseGuiScale);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(1);
        for (LiveWindow liveWindow : liveWindows) {
            liveWindow.preDrawWindow();
        }
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.popMatrix();
        super.drawScreen(x, y, f);
    }
}
