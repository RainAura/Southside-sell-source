package com.llamalad7.betterchat;

public final class BetterChat {
    private final static ChatSettings settings = new ChatSettings();
    public static ChatSettings getSettings() {
        return settings;
    }
}
