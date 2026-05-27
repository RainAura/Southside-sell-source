package dev.diona.southside.managers;

import dev.diona.southside.Southside;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.*;
import dev.diona.southside.module.modules.client.*;
import dev.diona.southside.module.modules.combat.*;
import dev.diona.southside.module.modules.misc.*;
import dev.diona.southside.module.modules.movement.*;
import dev.diona.southside.module.modules.nontoggleable.GameInformationTracker;
import dev.diona.southside.module.modules.nontoggleable.IRCUpdate;
import dev.diona.southside.module.modules.nontoggleable.SessionKeepAlive;
import dev.diona.southside.module.modules.player.*;
import dev.diona.southside.module.modules.render.*;
import dev.diona.southside.module.modules.world.Scaffold;
import net.minecraft.util.text.TextFormatting;
import top.fl0wowp4rty.phantomshield.annotations.Native;
import top.fl0wowp4rty.phantomshield.annotations.license.VirtualizationLock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ModuleManager {
    private final HashMap<Class<? extends BaseModule>, Module> modules = new HashMap<>();
    private final HashMap<Class<? extends BaseModule>, NonToggleableModule> nonToggleable = new HashMap<>();

    public ModuleManager() {
        Southside.eventBus.subscribe(this);
        this.initialize();
    }

    @Native
    @VirtualizationLock
    public void initialize() {
        // Client
        this.register(new Debug("Debug", "测试模块", Category.Client, true));
//        this.register(new HUD("HUD", "显示界面", Category.Client, false));
        this.register(new IRCFriend("IRCFriend", "IRC 友好", Category.Client, true));
        this.register(new Notification("Notification", "显示消息", Category.Client, false));
        this.register(new Radar("Radar", "雷达", Category.Client, true));
//        this.register(new MemoryMitigation("MemoryMitigation", "内存泄漏缓解", Category.Client, true));
        this.register(new Target("Target", "目标配置", Category.Client, false));

        // Combat
        this.register(new AimAssist("AimAssist", "瞄准辅助", Category.Combat, true));
        this.register(new AntiFireball("AntiFireball", "火球反弹", Category.Combat, true));
        this.register(new AutoClicker("AutoClicker", "连点器", Category.Combat, true));
        this.register(new AutoKnockBack("AutoKnockBack", "自动使用投射物对敌人进行击退", Category.Combat, true));
        this.register(new AutoL("AutoL", "嘲讽", Category.Combat, true));
        this.register(new BowAim("BowAim", "弓自瞄", Category.Combat, true));
        this.register(new Critical("Critical", "刀刀暴击", Category.Combat, true));
        this.register(new JumpCritical("JumpCritical", "自动跳跃刀爆", Category.Combat, true));
        this.register(new SuperKnockBack("SuperKnockBack", "超级击退", Category.Combat, true));
        this.register(new KillAura("KillAura", "杀戮光环", Category.Combat, true));
        this.register(new PreferWeapon("PreferWeapon", "武器选择", Category.Combat, true));
        this.register(new Reach("Reach", "增大攻击距离", Category.Combat, true));
        this.register(new SmartBlock("SmartBlock", "智能格挡弓箭或者TNT伤害", Category.Combat, true));
        this.register(new Velocity("Velocity", "反击退", Category.Combat, true));

        // Misc
        this.register(new AntiBot("AntiBot", "反假人", Category.Misc, true));
        this.register(new AntiSpammer("AntiSpammer", "自动过滤刷屏哥", Category.Misc, true));
        this.register(new AntiTigerMachine("AntiTigerMachine", "反老虎机", Category.Misc, true));
        this.register(new ChatBypass("ChatBypass", "聊天绕过", Category.Misc, true));
//        this.register(new Crasher("Crasher", "崩溃器", Category.Misc, true));
        this.register(new Disabler("Disabler", "禁用器", Category.Misc, true));
        this.register(new ExperimentalFeatures("ExperimentalFeatures", "实验性特性", Category.Misc, true));
        this.register(new HytProtocol("HytProtocol", "花雨庭协议", Category.Misc, true));
        this.register(new HackerDetector("HackerDetector", "检查其他玩家是否作弊", Category.Misc, true));
        this.register(new ItemDetector("ItemDetector", "物品检测", Category.Misc, true));
        this.register(new SilentDisconnect("SilentDisconnect", "静默断连", Category.Misc, true));
        this.register(new SpectatorAbuse("SpectatorAbuse", "开局飞", Category.Misc, true));
        this.register(new MCF("MCF", "鼠标中键玩家加白名单", Category.Misc, true));

        // Movement
        this.register(new AutoTeleport("AutoTeleport", "花雨庭开局出笼子", Category.Movement, true));
        this.register(new Flight("Flight", "飞行", Category.Movement, true));
        this.register(new InvMove("InvMove", "物品栏移动", Category.Movement, true));
        this.register(new KeepSprint("KeepSprint", "攻击不减速", Category.Movement, true));
        this.register(new NoSlow("NoSlow", "取消减速", Category.Movement, true));
        this.register(new NoWeb("NoWeb", "取消某些物品的减速", Category.Movement, true));
        this.register(new Speed("Speed", "加速", Category.Movement, true));
        this.register(new Sprint("Sprint", "自动疾跑", Category.Movement, true));
        this.register(new StrafeFix("StrafeFix", "移动修复", Category.Movement, true));
        this.register(new Stuck("Stuck", "停止移动", Category.Movement, true));

        // Player
        this.register(new Alink("Alink", "延迟收到的包", Category.Player, true));
        this.register(new AutoHeal("AutoHeal", "自动使用治疗物品", Category.Player, true));
        this.register(new AutoPotion("AutoPotion", "自动使用药水", Category.Player, true));
        this.register(new AntiVoid("AntiVoid", "虚空自救", Category.Player, true));
        this.register(new AutoPlay("AutoPlay", "自动进入下一局游戏", Category.Player, true));
        this.register(new AutoTool("AutoTool", "自动选择工具", Category.Player, true));
        this.register(new AutoWeapon("AutoWeapon", "自动选择武器", Category.Player, true));
        this.register(new BalancedTimer("BalancedTimer", "平衡时间齿轮", Category.Player, true));
        this.register(new Blink("Blink", "延迟发包", Category.Player, true));
        this.register(new ChestWreck("ChestWreck", "过河拆桥", Category.Player, true));
        this.register(new Stealer("Stealer", "自动拿物品", Category.Player, true));
        this.register(new NewStealer("NewStealer", "自动拿物品", Category.Player, true));
        this.register(new Eagle("Eagle", "自动蹲搭", Category.Player, true));
        this.register(new FastPlace("FastPlace", "快速放置", Category.Player, true));
        this.register(new LegitSpeed("LegitSpeed", "合法加速", Category.Player, true));
        this.register(new InvManager("InvManager", "物品栏整理", Category.Player, true));
        this.register(new KitSelector("KitSelector", "自动选择职业", Category.Player, true));
        this.register(new NoJumpDelay("NoJumpDelay", "没有跳跃延迟", Category.Player, true));
        this.register(new NoRotate("NoRotate", "无视服务器强制转头", Category.Player, true));
        this.register(new NoGuiClose("NoGuiClose", "阻止服务器关闭Gui", Category.Player, true));
//        this.register(new NoFall("NoFall", "无掉落伤害", Category.Player, true));
        this.register(new SpeedMine("SpeedMine", "快速挖掘", Category.Player, true));
//        this.register(new TellyBridge("TellyBridge", "木糖醇搭路", Category.Player, true));
        this.register(new Timer("Timer", "变速精灵(WIP)", Category.Player, true));
        this.register(new AllowEdit("AllowEdit", "允许在探险模式破坏方块", Category.Player, true));

        // Render
        this.register(new Ambience("Ambience", "调整客户端时间", Category.Render, true));
//        this.register(new BlockRateDisplay("BlockRateDisplay", "展示格挡率", Category.Render, true));
        this.register(new ESP("ESP", "玩家透视", Category.Render, true));
        this.register(new FreeCam("FreeCam", "免费相机", Category.Render, true));
        this.register(new FullBright("FullBright", "无限夜视", Category.Render, true));
        this.register(new MoreParticles("MoreParticles", "更多打击粒子", Category.Render, true));
        this.register(new MotionBlur("MotionBlur", "动态模糊", Category.Render, true));
        this.register(new NameTag("NameTag", "名称标签", Category.Render, true));
        this.register(new NoHurtCam("NoHurtCam", "取消受伤动画", Category.Render, true));
        this.register(new NoRender("NoRender", "取消渲染物品来提升性能", Category.Render, true));
        this.register(new OldHitting("OldHitting", "防砍动画", Category.Render, true));
        this.register(new Perspective("Perspective", "自由视角", Category.Render, true));
        this.register(new PotionEffects("PotionEffects", "药水显示", Category.Render, true));
        this.register(new SmoothZoom("SmoothZoom", "平滑缩放", Category.Render, true));
        this.register(new TargetHUD("TargetHUD", "目标显示", Category.Render, true));
//        this.register(new TestUIModule("TestUIModule", "测试", Category.Render, true));
        this.register(new ImmersiveOverlay("ImmersiveOverlay", "沉浸式界面", Category.Render, true));
        this.register(new ItemPhysics("ItemPhysics", "物理物品掉落", Category.Render, true));
        this.register(new FakePlayer("FakePlayer", "生成假人", Category.Misc, true));
        this.register(new StorageESP("StorageESP", "箱子透视", Category.Render, true));
        this.register(new Projectile("Projectile", "投掷物", Category.Render, true));
        this.register(new Rotations("Rotations", "转头渲染", Category.Render, true));
        this.register(new Xray("Xray", "青光眼", Category.Render, true));
//        this.register(new Overlay());
        this.register(new dev.diona.southside.module.modules.client.ArrayList("ArrayList", "功能列表", Category.Client, false));
        this.register(new ESP2D());
        this.register(new GameInformationTracker());
        this.register(new SessionKeepAlive());
        this.register(new IRCUpdate());

        this.register(new Scaffold("Scaffold", "自动搭路", Category.World, true));
        this.register(new Watermark());
        this.modules.forEach((e,e1)-> {
            e1.initialize();
            e1.initPostRunnable();
        });
    }

//    @EventListener
//    public void onKey(KeyEvent event) {
//        if (event.getKey() == Keyboard.KEY_GRAVE) {
//            Minecraft.getMinecraft().displayGuiScreen(new LivemessageGui());
//        }
//        for (Module module : modules.values()) {
//            if (module.getBind() == event.getKey()) {
//                module.toggle();
//            }
//        }
//    }

    private void register(BaseModule base) {
        if (base instanceof Module module) {
            for (final var field : module.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    final Object o = field.get(module);
                    if (o == null) continue;
                    if (o instanceof Value<?> value) module.getValues().add(value);
//                    if (o instanceof ValueList<?> values) values.forEach(value -> module.getValues().add((Value<?>) value));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            this.modules.put(module.getClass(), module);
        } else if (base instanceof NonToggleableModule nonToggleableModule) {
            Southside.eventBus.subscribe(nonToggleableModule);
            this.nonToggleable.put(base.getClass(), nonToggleableModule);
        }
    }

    public final List<Module> getModulesByCategory(Category category) {
        final ArrayList<Module> mods = new ArrayList<>();
        for (final var module : this.modules.values()) {
            if (module.getCategory() == category) mods.add(module);
        }
        mods.sort(Comparator.comparing(Module::getName));
        return mods;
    }

    public final Module getModuleByName(final String name) {
        for (final var module : this.modules.values()) {
            if (module.getName().equalsIgnoreCase(name)) return module;
        }
        return null;
    }

    public final Module getModuleByClass(final Class<? extends Module> moduleClass) {
        return modules.get(moduleClass);
    }

    public List<Module> getModules() {
        final ArrayList<Module> mods = new ArrayList<>(modules.values());
        mods.sort(Comparator.comparing(Module::getName));
        return mods;
    }

    public String format(Module module) {
        return module.getSuffix().isEmpty() ? module.getName() : String.format("%s %s%s", module.getName(), TextFormatting.GRAY, module.getSuffix());
    }

    public String formatRaw(Module module) {
        return module.getSuffix().isEmpty() ? module.getName() : String.format("%s %s", module.getName(), module.getSuffix());
    }

    public ArrayList<Module> getopenValues() {
        ArrayList<Module> arrayList = new ArrayList<>();
        for (Module class88 : modules.values()) {
            if (!class88.openValues) continue;
            arrayList.add(class88);
        }
        return arrayList;
    }

    public void toggleAllListeners() {
        modules.values().forEach(module -> {
            if (module.isEnabled()) {
                module.onEnable();
            }
//            else {
//                module.onDisable();
//            }
        });
    }
}
