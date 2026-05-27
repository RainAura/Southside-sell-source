package dev.diona.southside.util.chat;

import com.google.gson.JsonObject;

public class ChatMessage {
    private String username, message;
    private long timestamp;
    public ChatMessage(JsonObject data) {
        username = data.get("username").getAsString();
        message = data.get("message").getAsString();
        timestamp = data.get("timestamp").getAsLong();

    }

    public String getUsername() {
        return this.username;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message.replace('§', '&');
    }
}
