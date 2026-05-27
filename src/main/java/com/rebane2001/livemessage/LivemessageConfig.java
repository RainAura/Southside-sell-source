package com.rebane2001.livemessage;

public class LivemessageConfig {

    public static final NotificationSettings notificationSettings = new NotificationSettings();

    public static class NotificationSettings {
        public boolean toastsFromFriends = true;

        public boolean toastsFromChats = false;

        public boolean toastsFromBlocked = false;


        public boolean soundsFromFriends = true;

        public boolean soundsFromChats = false;

        public boolean soundsFromBlocked = false;
    }


    public static final HideSettings hideSettings = new HideSettings();

    public static class HideSettings {

        public boolean hideFromFriends = false;

        public boolean hideFromChats = false;
        public boolean hideFromBlocked = true;
    }


    public static final GuiSettings guiSettings = new GuiSettings();

    public static class GuiSettings {
        public int guiScale = 2;
    }

    public static final OtherSettings otherSettings = new OtherSettings();

    public static class OtherSettings {

        public boolean sneakRightClick = false;
        public boolean readOnReply = true;
        public boolean timestampPatch = true;
    }
}
