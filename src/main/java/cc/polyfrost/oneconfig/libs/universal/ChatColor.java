package cc.polyfrost.oneconfig.libs.universal;

import java.awt.*;

public class ChatColor {
    public static final ChatColor BLACK = new ChatColor("BLACK", 0, '0', new Color(0), false, 4);
    public static final ChatColor DARK_BLUE = new ChatColor("DARK_BLUE", 1, '1', new Color(170), false, 4);
    public static final ChatColor DARK_GREEN = new ChatColor("DARK_GREEN", 2, '2', new Color(43520), false, 4);
    public static final ChatColor DARK_AQUA = new ChatColor("DARK_AQUA", 3, '3', new Color(43690), false, 4);
    public static final ChatColor DARK_RED = new ChatColor("DARK_RED", 4, '4', new Color(0xAA0000), false, 4);
    public static final ChatColor DARK_PURPLE = new ChatColor("DARK_PURPLE", 5, '5', new Color(0xAA00AA), false, 4);
    public static final ChatColor GOLD = new ChatColor("GOLD", 6, '6', new Color(0xFFAA00), false, 4);
    public static final ChatColor GRAY = new ChatColor("GRAY", 7, '7', new Color(0xAAAAAA), false, 4);
    public static final ChatColor DARK_GRAY = new ChatColor("DARK_GRAY", 8, '8', new Color(0x555555), false, 4);
    public static final ChatColor BLUE = new ChatColor("BLUE", 9, '9', new Color(0x5555FF), false, 4);
    public static final ChatColor GREEN = new ChatColor("GREEN", 10, 'a', new Color(0x55FF55), false, 4);
    public static final ChatColor AQUA = new ChatColor("AQUA", 11, 'b', new Color(0x55FFFF), false, 4);
    public static final ChatColor RED = new ChatColor("RED", 12, 'c', new Color(0xFF5555), false, 4);
    public static final ChatColor LIGHT_PURPLE = new ChatColor("LIGHT_PURPLE", 13, 'd', new Color(0xFF55FF), false, 4);
    public static final ChatColor YELLOW = new ChatColor("YELLOW", 14, 'e', new Color(0xFFFF55), false, 4);
    public static final ChatColor WHITE = new ChatColor("WHITE", 15, 'f', new Color(0xFFFFFF), false, 4);
    public static final ChatColor MAGIC = new ChatColor("MAGIC", 16, 'k', null, true, 2);
    public static final ChatColor BOLD = new ChatColor("BOLD", 17, 'l', null, true, 2);;
    public static final ChatColor STRIKETHROUGH = new ChatColor("STRIKETHROUGH", 18, 'm', null, true, 2);
    public static final ChatColor UNDERLINE = new ChatColor("UNDERLINE", 19, 'n', null, true, 2);
    public static final ChatColor ITALIC = new ChatColor("ITALIC", 20, 'o', null, true, 2);
    public static final ChatColor RESET = new ChatColor("RESET", 21, 'r', null, false, 6);

    private final char char1;
    private final Color color;
    private final boolean isFormat;
    public static final char COLOR_CHAR = '§';

    public ChatColor(String string, int n, char c, Color color, boolean bl, int n2) {
        this(c, color, bl);
    }

    public ChatColor(char c, Color color, boolean isFormat) {
        this.char1 = c;
        this.color = color;
        this.isFormat = isFormat;
    }

    public String getChar() {
        return "" + COLOR_CHAR + char1;

    }

    //    static {
//        BLACK = new ChatColor("BLACK", 0, '0', new Color(0), false, 4);
//        DARK_BLUE = new ChatColor("DARK_BLUE", 1, '1', new Color(170), false, 4);
//        DARK_GREEN = new ChatColor("DARK_GREEN", 2, '2', new Color(43520), false, 4);
//        DARK_AQUA = new ChatColor("DARK_AQUA", 3, '3', new Color(43690), false, 4);
//        DARK_RED = new ChatColor("DARK_RED", 4, '4', new Color(0xAA0000), false, 4);
//        DARK_PURPLE = new ChatColor("DARK_PURPLE", 5, '5', new Color(0xAA00AA), false, 4);
//        GOLD = new ChatColor("GOLD", 6, '6', new Color(0xFFAA00), false, 4);
//        GRAY = new ChatColor("GRAY", 7, '7', new Color(0xAAAAAA), false, 4);
//        DARK_GRAY = new ChatColor("DARK_GRAY", 8, '8', new Color(0x555555), false, 4);
//        BLUE = new ChatColor("BLUE", 9, '9', new Color(0x5555FF), false, 4);
//        GREEN = new ChatColor("GREEN", 10, 'a', new Color(0x55FF55), false, 4);
//        AQUA = new ChatColor("AQUA", 11, 'b', new Color(0x55FFFF), false, 4);
//        RED = new ChatColor("RED", 12, 'c', new Color(0xFF5555), false, 4);
//        LIGHT_PURPLE = new ChatColor("LIGHT_PURPLE", 13, 'd', new Color(0xFF55FF), false, 4);
//        YELLOW = new ChatColor("YELLOW", 14, 'e', new Color(0xFFFF55), false, 4);
//        WHITE = new ChatColor("WHITE", 15, 'f', new Color(0xFFFFFF), false, 4);
//        MAGIC = new ChatColor("MAGIC", 16, 'k', null, true, 2);
//        BOLD = new ChatColor("BOLD", 17, 'l', null, true, 2);
//        STRIKETHROUGH = new ChatColor("STRIKETHROUGH", 18, 'm', null, true, 2);
//        UNDERLINE = new ChatColor("UNDERLINE", 19, 'n', null, true, 2);
//        ITALIC = new ChatColor("ITALIC", 20, 'o', null, true, 2);
//        RESET = new ChatColor("RESET", 21, 'r', null, false, 6);
//    }
}
