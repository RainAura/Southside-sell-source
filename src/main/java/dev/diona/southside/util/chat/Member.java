package dev.diona.southside.util.chat;

public class Member {
    private final String name;
    private final String client;
    private boolean friend;
    private String mcName;
    private String uuid;

    private String location;

    public Member(String name, String client, String location) {
        this.name = name;
        this.client = client;
        this.location = location;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getClient() {
        return client;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMcName() {
        return mcName;
    }

    public void setMcName(String mcName) {
        this.mcName = mcName;
    }

    public String getLocation() {
        return location;
    }
}
