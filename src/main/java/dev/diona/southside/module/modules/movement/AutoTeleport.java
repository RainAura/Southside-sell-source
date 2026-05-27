package dev.diona.southside.module.modules.movement;

import dev.diona.southside.Southside;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.event.events.UpdateEvent;
import dev.diona.southside.event.events.WorldEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.player.Alink;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.ChatUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.BlockGlass;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.diona.southside.Southside.MC.mc;

public class AutoTeleport extends Module {
    private static final String pattern = "§f地图:§a§a(.*)";
//    public BooleanValue shiftValue = new Switch("Shift TP", true);
    private final HashMap<String, Vec3d> centers = new HashMap<>();
    public AutoTeleport(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        centers.put("天空之城", new Vec3d(106.5, 172, -53.5));
        centers.put("虚空农庄", new Vec3d(63.5, 81, 57.5));
        centers.put("冰河战场", new Vec3d(77.5, 107, 51.5));
        centers.put("圣火祭坛", new Vec3d(1.5, 166, 2.5));
        centers.put("冰河之舟", new Vec3d(12.5, 122, 12.5));
        centers.put("雪之魂", new Vec3d(12.5, 122, 12.5));
        centers.put("神圣遗迹", new Vec3d(1.5, 175, 5.5));
        centers.put("荒芜极地", new Vec3d(-29.5, 148, -41.5));
        centers.put("钻石祭坛", new Vec3d(14.5, 127, -15.5));
        centers.put("像素沼泽", new Vec3d(-70.5, 47, 63.5));
        centers.put("钻石之心", new Vec3d(6.5, 101, -8.5));
        centers.put("小船", new Vec3d(-76.5, 33, 50.5));
        centers.put("沙舟", new Vec3d(-44.5, 84, 54.5));
        centers.put("边境岗哨", new Vec3d(-3, 172, 3));
        centers.put("遗迹之轮", new Vec3d(-6.5, 84, -0.5));
        centers.put("炽炎地狱", new Vec3d(-73, 106, 75.5));
    }

    @Override
    public boolean onEnable() {
        alinking = false;
        if (mc.player.capabilities.isFlying) {
            alinking = true;
            Southside.moduleManager.getModuleByClass(Alink.class).setEnable(true);
            Southside.moduleManager.getModuleByClass(Flight.class).setEnable(true);
            this.move(0, 5, 0);
        } else {
            this.setEnable(false);
        }

        return true;
    }

    @Override
    public boolean onDisable() {
        if (Alink.isInstanceEnabled()) {
            Southside.moduleManager.getModuleByClass(Flight.class).setEnable(false);
            return false;
        }
        return true;
    }


//    public boolean waitingScoreboard = true;
//    private boolean teleported = false;

    @EventListener
    public void onWorld(WorldEvent event) {
//        waitingScoreboard = true;
//        teleported = false;
//        teleportCount = 0;
        alinking = false;
    }

    private String mapName;
//    private final TimerUtil timer = new TimerUtil();
//
//    private int teleportCount = 0;

//    @EventListener
//    public void onUpdate(UpdateEvent event) {
//        if (mc.player == null) return;
//        if (mc.player.isSpectator()) return;
//        if (mc.player.isCreative()) return;
//        if (mc.gameSettings.keyBindSneak.isPressed()) {
//            if (mc.player.capabilities.isFlying) {
//                move(0, 5, 0);
//            }
//        }
//    }

    private boolean alinking = false;

    @EventListener
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;
//        if (mc.player.isSpectator()) return;
//        if (mc.player.isCreative()) return;
//        if (mc.player.capabilities.isFlying && this.hasGlassBelow() && mc.ingameGUI.displayedSubTitle.equals("准备战斗...")) {
//            alinking = true;
//            Southside.moduleManager.getModuleByClass(Alink.class).setEnable(true);
//            Southside.moduleManager.getModuleByClass(Flight.class).setEnable(true);
//            this.move(0, 5, 0);
//        }
        if (alinking && !Flight.isInstanceEnabled()) {
            alinking = false;
            Southside.moduleManager.getModuleByClass(Alink.class).setEnable(false);
            this.setEnable(false);
        }
    }

    private boolean isGlass(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() instanceof BlockGlass;
    }

    private boolean hasGlassBelow() {
        return this.isGlass(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)) ||
                this.isGlass(new BlockPos(mc.player.posX, mc.player.posY - 2, mc.player.posZ));
    }

//    @EventListener
//    public void onPacket(PacketEvent event) {
//        if (event.getPacket() instanceof SPacketTitle packet) {
//            if (packet.getMessage() != null && packet.getMessage().toString().contains("战斗开始") && !teleported) {
//                teleportCount = 0;
//                teleported = true;
//            }
//        }
//    }

    private void move(double x, double y, double z) {
        mc.player.setPosition(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
    }

    private String getMapName(SPacketTeams packet) {
        for (String str : packet.getPlayers()) {
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(str);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return "";
    }
}
