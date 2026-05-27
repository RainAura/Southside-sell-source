package dev.diona.southside.util.misc;

import java.util.regex.Pattern;

public class TextUtil {
    private static final Pattern sb = Pattern.compile("(?i)" + '\u00a7' + "[0-9A-FK-OR]");
    public static String removeFormattingCodes(String text) {
        return sb.matcher(text).replaceAll("");
    }

    public static String read(int number) {
        String[] num = {"一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] unit = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千", "万亿"};
        String result = String.valueOf(number);
        char[] ch = result.toCharArray();
        StringBuilder str = new StringBuilder();
        int length = ch.length;
        for (int i = 0; i < length; i++) {
            int c = (int) ch[i] - 48;
            if (c != 0) {
                str.append(num[c - 1]).append(unit[length - i - 1]);
            }
        }
        return str.toString().replaceFirst("^一十", "十");
    }
}
