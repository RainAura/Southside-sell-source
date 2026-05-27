package dev.diona.southside.util.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatChannel {
    private final String name;
    private final Queue<ChatMessage> messages = new ConcurrentLinkedQueue<>();
    private boolean canTalk;
    public ChatChannel(final JsonObject data) {
        name = data.get("name").getAsString();
        canTalk = data.get("canTalk").getAsBoolean();

        JsonArray messagesArray = data.get("messages").getAsJsonArray();
        for (JsonElement jsonElement : messagesArray) {
            messages.add(new ChatMessage(jsonElement.getAsJsonObject()));
        }
    }

    public String getName() {
        return name;
    }

    public Queue<ChatMessage> getMessages() {
        return messages;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        if (messages.size() > 100) {
            messages.poll();
        }
    }

    public boolean isCanTalk() {
        return canTalk;
    }
}
