package dev.diona.southside;

import cc.polyfrost.oneconfig.config.options.WrappedValue;
import dev.diona.southside.util.render.ChromaJS;
import dev.diona.southside.util.text.TextUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
//        WrappedValue<String> sb = new WrappedValue<String>("666", "666");
//        System.out.println(sb.getTargetClass().isAssignableFrom(Boolean.class));
        ChromaJS.Scale scale = new ChromaJS.Scale(new Color(0x1f005c), new Color(0xffb56b), 8);
        for (int i = 0; i < 8; i++) {
            System.out.println(scale.colors.get(i));
        }
    }
}
