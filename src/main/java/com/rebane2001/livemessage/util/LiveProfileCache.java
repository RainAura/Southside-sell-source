package com.rebane2001.livemessage.util;

import dev.diona.southside.util.chat.Chat;
import dev.diona.southside.util.chat.Member;

public class LiveProfileCache {

    public static class LiveProfile {
        public String username;
        public String client;
        public String mcName;
        public String uuid;
    }

    public static LiveProfile getLiveprofileFromName(String username) {
        for (String s : Chat.getInstance().getOnlineMembers().keySet()) {
            System.out.println(s);
        }
        final var member = Chat.getInstance().getOnlineMembers().get(username);
        final var liveProfile = new LiveProfile();
        liveProfile.username = member.getName();
        liveProfile.uuid = member.getUuid();
        liveProfile.mcName = member.getMcName();
        liveProfile.client = member.getClient();
        return liveProfile;
    }
}
