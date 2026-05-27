package dev.diona.southside.util.chat;

public class Xor {
    public static String e(String text) {
        final var KEY = "000000";
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            char keyChar = KEY.charAt(i % KEY.length());
            encrypted.append((char) (c ^ keyChar));
        }
        return encrypted.toString();
    }
}
