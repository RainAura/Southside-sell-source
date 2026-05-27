package dev.diona.southside.util.chat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rebane2001.livemessage.gui.LivemessageGui;
import dev.diona.southside.util.player.ChatUtil;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Chat {
    private static Chat instance;
    private static String token;
    private final ConcurrentHashMap<String, Member> onlineMembers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ChatChannel> channelsFromName = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<ChatChannel> channels = new CopyOnWriteArrayList<>();
    private static Thread rebootThread = null;
    private ChatWebsocketClient websocketClient;

    public static Chat getInstance() {
        if (instance == null) {
            new Chat("0000000000000000000000000000000000000000000000000000000000000000");
        }
        return instance;
    }

    public Chat(String token2) {
        token = token2;
        instance = this;
        try {
            websocketClient = new ChatWebsocketClient(this, token);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleMessage(JsonObject data) {
        //System.out.println(new Gson().toJson(data));
        switch (data.get("type").getAsString()) {
            case "channelsAndRanks": {
                channels.clear();
                channelsFromName.clear();
                for (final var val : data.get("channels").getAsJsonArray()) {
                    ChatChannel channel = new ChatChannel(val.getAsJsonObject());
                    channels.add(channel);
                    channelsFromName.put(channel.getName(), channel);
                }
                break;
            }
            case "newMessage": {
                String channelName = data.get("channel").getAsString();
                ChatChannel channel = channelsFromName.get(channelName);
                final var msg = new ChatMessage(data.get("msg").getAsJsonObject());
                //System.out.println(msg.getMessage());
                channel.addMessage(msg);
                LivemessageGui.newMessage(channelName, msg, false);
                break;
            }
            case "deleteMessage": {
                String username = data.get("username").getAsString();
                long timestamp = data.get("timestamp").getAsLong();
                for (ChatChannel agoraChannel : channels) {
                    agoraChannel.getMessages().removeIf(c -> c.getUsername().equals(username) && c.getTimestamp() == timestamp);
                }
                break;
            }
            case "userConnected": {
                JsonObject userJson = data.get("user").getAsJsonObject();
                final var name = userJson.get("name").getAsString();
                final var client = userJson.get("client").getAsString();
                final var location = userJson.get("location").getAsString();
                final var member = new Member(name, client, location);

                onlineMembers.put(name, member);
                break;
            }
            case "userDisconnected": {
                JsonObject userJson = data.get("user").getAsJsonObject();
                String username = userJson.get("name").getAsString();
                onlineMembers.remove(username);
                break;
            }
            case "userUpdate": {
                JsonObject userJson = data.get("user").getAsJsonObject();
                final var name = userJson.get("name").getAsString();
                var member = onlineMembers.get(name);
                if (member == null) {
                    member = new Member(userJson.get("name").getAsString(), userJson.get("client").getAsString(), userJson.get("location").getAsString());
                }
                member.setFriend(userJson.get("friend").getAsBoolean());
                member.setUuid(userJson.get("uuid").getAsString());
                member.setMcName(userJson.get("mcName").getAsString());
                onlineMembers.put(name, member);
                break;
            }
        }
    }

    public final boolean isOpen() {
        if (websocketClient != null) {
            return websocketClient.isOpen();
        } else {
            return false;
        }
    }

    public void sendRaw(String msg) {
        websocketClient.send(Xor.e(msg));
    }

    public void sendMessage(ChatChannel channel, String msg) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "message");
        data.addProperty("channel", channel.getName());
        data.addProperty("msg", msg);
        websocketClient.send(Xor.e(data.toString()));
    }

    public void deleteMessage(String messageUsername, long messageTimestamp) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "deleteMessage");
        data.addProperty("username", messageUsername);
        data.addProperty("timestamp", messageTimestamp);
        websocketClient.send(Xor.e(data.toString()));
    }

    public ConcurrentHashMap<String, ChatChannel> getChannelsFromName() {
        return channelsFromName;
    }

    public CopyOnWriteArrayList<ChatChannel> getChannels() {
        return channels;
    }

    public static synchronized void onConnectionLost() {
        if (rebootThread == null) {
            rebootThread = new Thread(() -> {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ChatUtil.info("IRC 连接断开，重连中...");
                instance = new Chat(token);
                rebootThread = null;
            });
            rebootThread.start();
        }
    }

    public ConcurrentHashMap<String, Member> getOnlineMembers() {
        return onlineMembers;
    }
}
