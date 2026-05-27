package dev.diona.southside.util.quickmacro.vexview;

import dev.diona.southside.util.quickmacro.metadatas.PartyRequestMetadata;
import dev.diona.southside.util.quickmacro.metadatas.VexViewMetadata;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

public class VexViewReader {
    public static boolean inviting = false;
    private final String[] elements;
    public final boolean invited;
    public final String inviter;
    public final boolean sign;
    public final boolean list;
    public final ArrayList<PartyRequestMetadata> requests;

    public VexViewReader(ByteBuf byteBuf) throws IOException {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String result = decode(bytes);
        if (!result.contains("[but]手动输入")) {
            sign = result.contains("[gui]https://img.166.net/gameyw-misc/opd/squash/20221221/104939-4q3d0pgm59.png");
            if (sign) {
                result = result.replace("[but]", "[but]sign");
            }
            list = result.contains("[gui]https://ok.166.net/gameyw-misc/opd/squash/20210915/195203-c2npy8skq6.png");
            requests = new ArrayList<>();
            if (list) {
                result = result.replace("null<#>[but]", "null<#>[denyButton]");
                result = result.replace("[but]", "[but]accept");
                result = result.replace("[denyButton]", "[but]deny");
            }
        } else {
            sign = false;
            list = false;
            requests = null;
        }
        if (result.contains("邀请组队")) {
            inviting = true;
        }
        elements = result.split("<&>");
        if (list) {
            boolean nextDeny = false;
            String cacheName = "";
            int cacheAccept = -1;
            for (int i = 0; i < elements.length; i++) {
                String element = elements[i];
                if (element.equals("true<#>[but]accept申请列表")) continue;
                if (!nextDeny) {
                    if (element.contains("<#>[but]accept")) {
                        cacheName = element.substring(0, element.length() - 14);
                        cacheAccept = i += 6;
                        nextDeny = true;
                    }
                } else if (element.contains("<#>[but]deny")) {
                    nextDeny = false;
//                    requests.add(new PartyRequestMetadata(cacheName, String.valueOf(cacheAccept), String.valueOf(i += 6)));
                    requests.add(new PartyRequestMetadata(cacheName, elements[cacheAccept], elements[i += 6]));
                }
            }
        }
        int index = this.containsString("邀请你加入队伍");
        if (index != -1) {
            invited = true;
            inviter = this.elements[index - 3].replace("\2476：<#>[txt]50", "").replace("\2476玩家 \2473\247l", "");
        } else {
            invited = false;
            inviter = "";
        }
    }

    private String decode(byte[] bytes) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPInputStream gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
        byte[] array = new byte[256];
        int read;
        while ((read = gZIPInputStream.read(array)) >= 0) {
            byteArrayOutputStream.write(array, 0, read);
        }
        return byteArrayOutputStream.toString("UTF-8");
    }

    public boolean containsButtons(String... buttons) {
        for (String button : buttons) {
            if (!this.containsButton(button)) {
                return false;
            }
        }
        return true;
    }

    public boolean containsButton(String btn) {
        for (String element : elements) {
            if (element.endsWith("[but]" + btn)) {
                return true;
            }
        }
        return false;
    }

    public VexViewMetadata getButton(String name) {
        for (int i = 0; i < this.elements.length; ++i) {
            String e = this.elements[i];
            if (e.endsWith("[but]" + name)) {
                return new VexViewMetadata(name, this.elements[i + 6]);
            }
        }
        return null;
    }

    public int getButtonIndex(String name) {
        for (int i = 0; i < this.elements.length; ++i) {
            String e = this.elements[i];
            if (e.endsWith("[but]" + name)) {
                return i;
            }
        }
        return 0;
    }

    public int containsString(String s) {
        for (int i = 0; i < this.elements.length; ++i) {
            String e = this.elements[i];
            if (e.contains(s)) {
                return i;
            }
        }
        return -1;
    }

    public String getElement(int index) {
        return elements[index];
    }
}
