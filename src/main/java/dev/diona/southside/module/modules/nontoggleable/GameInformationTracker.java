package dev.diona.southside.module.modules.nontoggleable;

import com.google.gson.JsonObject;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.PacketType;
import dev.diona.southside.event.events.*;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.NonToggleableModule;
import dev.diona.southside.module.modules.client.Target;
import dev.diona.southside.util.authentication.AuthenticationStatus;
import dev.diona.southside.util.authentication.WebUtil;
import dev.diona.southside.util.chat.Chat;
import dev.diona.southside.util.misc.TextUtil;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.ChatUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.login.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.*;

import static dev.diona.southside.Southside.MC.mc;

public class GameInformationTracker extends NonToggleableModule {
    private final static String SERVER_ENDPOINT = "https://api.south.service";
    private final static HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private final TimerUtil delay = new TimerUtil();
    private EntityPlayer target;

    private Game currentGameType = Game.NONE;

    enum Game {
        NONE, BEDWARS, SKYWARS, KITBATTLE, PIT
    }

    public GameInformationTracker() {
        super("GameInformationTracker", "Southside封神榜基础组件");
        delay.reset();
    }

    @EventListener
    public void onWorldEvent(final WorldEvent event) {
        currentGameType = Game.NONE;
    }

    @EventListener
    public void onTickEvent(final TickEvent event) {
        if (delay.hasReached(1000 * 60 * 5)) {
            delay.reset();
            sent("time");
        }
        Scoreboard scoreboard = Minecraft.getMinecraft().world.getScoreboard();
        if (scoreboard != null) {
            if (scoreboard.getObjective("info") != null) {
                boolean contains = scoreboard.getObjective("info").getDisplayName().contains("空岛战争");
                if (contains) updateCurrentGameType(Game.SKYWARS);
            } else if (scoreboard.getObjective("KB") != null) {
                boolean contains = scoreboard.getObjective("KB").getDisplayName().contains("职业战争");
                if (contains) updateCurrentGameType(Game.KITBATTLE);
            } else if (scoreboard.getObjective(mc.player.getName()) != null) {
                updateCurrentGameType(Game.BEDWARS);
            } else if (scoreboard.getObjective("Scoreboard") != null) {
                boolean contains = scoreboard.getObjective("Scoreboard").getDisplayName().contains("天坑");
                if (contains) updateCurrentGameType(Game.PIT);
            }
        }
    }

    @EventListener
    public final void onServerDisconnectedEvent(final ServerDisconnectedEvent event) {
        final var unformattedText = event.getReason().getUnformattedText();
        System.out.println(unformattedText);
        if (unformattedText.contains("ban") || unformattedText.contains("不正当的游戏行为")) {
            sent("ban");
        }
    }

    @EventListener
    public final void onPacketEvent(final PacketEvent event) {
        if (event.getPacket() instanceof SPacketPlayerListHeaderFooter packet) {
            if (packet.getHeader().getUnformattedText().contains("起床战争")) {
                updateCurrentGameType(Game.BEDWARS);
            }
        }
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        if (event.getState() == EventState.POST) return;

        if (Minecraft.getMinecraft().player.isSpectator()) {
            target = null;
        }

        if (Minecraft.getMinecraft().getCurrentServerData() == null || Minecraft.getMinecraft().getCurrentServerData().serverIP.equals("mc.loyisa.cn")) return;

        if (target != null && !Minecraft.getMinecraft().world.playerEntities.contains(target) && target.isDead) {
            sent("kill");
            target = null;
        }
    }

    @EventListener
    public void onAttack(AttackEvent event) {
        final Entity target = event.getTargetEntity();

        if (target instanceof EntityPlayer && Target.isTarget(target)) {
            this.target = (EntityPlayer) target;
        }
    }

    private void updateCurrentGameType(final Game input) {
        if (currentGameType != input) {
            ChatUtil.sendText("排行榜记录器切换到" + input.name() + "模式");
            currentGameType = input;
        }
    }
    private static String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String requestSign(Map<String, String> params) {
        // 按照 key 重排参数
        Map<String, String> sortedParams = new TreeMap<>(params);
        // 序列化参数
        StringBuilder queryBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (!queryBuilder.isEmpty()) {
                queryBuilder.append('&');
            }
            queryBuilder
                    .append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return generateMD5(queryBuilder.toString());
    }

    private void sent(final String type) {
        if (currentGameType == Game.NONE) return;
        WebUtil.rawApi("/statistics/kill", new WebUtil.CallbackWithParams<>() {
            @Override
            public HashMap<String, String> params(HashMap<String, String> params) {
                params.put("token", AuthenticationStatus.INSTANCE.token);
                params.put("sessionToken", AuthenticationStatus.INSTANCE.session);
                params.put("t", type);
                params.put("game", currentGameType.name());
                params.put("param1", String.valueOf(System.currentTimeMillis()));
                final var sign = requestSign(params);
                params.put("sign", sign);
                return params;
            }

            @Override
            public void onSuccess(String e) {
                if (e != null) System.out.println(e);
            }

            @Override
            public void onFail(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
