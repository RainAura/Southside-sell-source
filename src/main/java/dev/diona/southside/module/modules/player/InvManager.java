package dev.diona.southside.module.modules.player;

import cc.polyfrost.oneconfig.config.options.impl.Dropdown;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import cc.polyfrost.oneconfig.config.options.impl.Switch;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.MotionEvent;
import dev.diona.southside.event.events.PacketEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.modules.movement.NoSlow;
import dev.diona.southside.module.modules.player.invmanager.SubComponent;
import dev.diona.southside.module.modules.player.invmanager.subcomponents.*;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.player.InventoryUtil;
import dev.diona.southside.util.player.MovementUtil;
import dev.diona.southside.util.quickmacro.HytUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.*;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketOpenWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvManager extends Module {
    private static InvManager INSTANCE;
    public final Slider minDelayValue = new Slider("Min Delay", 0D, 0D, 300D, 1D);
    public final Slider maxDelayValue = new Slider("Max Delay", 0D, 0D, 300D, 1D);
    public final Switch openValue = new Switch("Open Inventory", false);
    public final Switch noMoveValue = new Switch("No Move", false);
    public final Switch noUsingValue = new Switch("No Using", true);
    public final Slider startDelayValue = new Slider("Start Delay", 500, 0, 2000, 1);


    public final Dropdown slot1Value = (getItemValue("Slot 1", "Weapon"));
    public final Dropdown slot2Value = (getItemValue("Slot 2", "Block"));
    public final Dropdown slot3Value = (getItemValue("Slot 3", "Gapple"));
    public final Dropdown slot4Value = (getItemValue("Slot 4", "Bow"));
    public final Dropdown slot5Value = (getItemValue("Slot 5", "Axe"));
    public final Dropdown slot6Value = (getItemValue("Slot 6", "Pickaxe"));
    public final Dropdown slot7Value = (getItemValue("Slot 7", "Potion"));
    public final Dropdown slot8Value = (getItemValue("Slot 8", "Projectile"));
    public final Dropdown slot9Value = (getItemValue("Slot 9", "Pearl"));


    public final List<Dropdown> slotValue = new ArrayList<>();
    public final Switch inferiorValue = new Switch("Keep Inferior Items", false);
    public final Switch armorValue = new Switch("Wear Armors", true);
    public final Switch debuffValue = new Switch("Keep Debuff Potions", true);
    public final Switch arrowValue = new Switch("Keep Arrows", true);
    public final Switch throwableValue = new Switch("Keep Throwable", true);
    public final Switch bedWarsValue = new Switch("Keep Bed Wars Items", true);
    public final Switch serverValue = new Switch("Keep Server Items", true);
    private final List<SubComponent> subComponents = new ArrayList<>();
    private final List<ArmorComponent> armorComponents = new ArrayList<>();
    private final HashMap<String, Integer> itemSlot = new HashMap<>();

    private final static String serverItems = ".*(选择游戏|加入游戏|左键|右键|点击|菜单|离开|再来一局|退出).*";

    private static final List<Item> bedWarsItem = List.of(new Item[]{Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Items.EMERALD, Items.NETHER_STAR, Items.SHEARS});

    public final TimerUtil timer = new TimerUtil();
    private final TimerUtil startTimer = new TimerUtil();
    private long delay = 0;

    public InvManager(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);

        subComponents.add(new WeaponComponent(this));
//        subComponents.add(new SwordComponent(this));
        subComponents.add(new BlockComponent(this));
        subComponents.add(new AxeComponent(this));
        subComponents.add(new PickaxeComponent(this));
        subComponents.add(new BowComponent(this));
        subComponents.add(new GappleComponent(this));
        subComponents.add(new FoodComponent(this));
        subComponents.add(new PearlComponent(this));
        subComponents.add(new PotionComponent(this));
//        subComponents.add(new SharpAxeComponent(this));
        subComponents.add(new TNTComponent(this));
//        subComponents.add(new KnockBackComponent(this));
        subComponents.add(new ProjectileComponent(this));

        armorComponents.add(new ArmorComponent(5, "Helmet", this));
        armorComponents.add(new ArmorComponent(6, "Chestplate", this));
        armorComponents.add(new ArmorComponent(7, "Leggings", this));
        armorComponents.add(new ArmorComponent(8, "Boots", this));

        INSTANCE = this;
    }

    @Override
    public void initPostRunnable() {
        super.initPostRunnable();
        addDependency(startDelayValue.getLabel(), () -> !openValue.getValue());

        slotValue.add(slot1Value);
        slotValue.add(slot2Value);
        slotValue.add(slot3Value);
        slotValue.add(slot4Value);
        slotValue.add(slot5Value);
        slotValue.add(slot6Value);
        slotValue.add(slot7Value);
        slotValue.add(slot8Value);
        slotValue.add(slot9Value);

        this.addRangedValueRestrict(minDelayValue, maxDelayValue);
    }

    private boolean waitingResponse = false;
    private TimerUtil rightClickTimer = new TimerUtil();

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock packet) {
            Block block = mc.world.getBlockState(packet.getPos()).getBlock();
            if (block instanceof BlockChest || block instanceof BlockFurnace || block instanceof BlockAnvil || block instanceof BlockBrewingStand) {
                waitingResponse = true;
                rightClickTimer.reset();
            }
        }
        if (event.getPacket() instanceof SPacketOpenWindow packet) {
            waitingResponse = false;
        }
    }

    public void swap(int slot, int hSlot) {
//        ChatUtil.info("SWAP");
//        open();
        InventoryUtil.swap(slot, hSlot);
        timer.reset();
//        close();
    }

    public void open() {
        if (mc.currentScreen instanceof GuiInventory) return;
        mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.OPEN_INVENTORY));
    }

    public void close() {
        if (mc.currentScreen instanceof GuiInventory) return;
        mc.getConnection().sendPacket(new CPacketCloseWindow(mc.player.inventoryContainer.windowId));
    }

    public void drop(int slot) {
//        ChatUtil.info("DROP");
        InventoryUtil.drop(slot);
    }

    public void click(int slot, int button, boolean shift) {
        InventoryUtil.click(slot, button, shift);
    }

    @EventListener
    public void onMotion(MotionEvent event) {

        if (event.getState() != EventState.PRE) return;

        if (mc.player.isCreative() || mc.player.isSpectator()) return;

        if (HytUtil.isInHyt()) return;

        if (!AutoWeapon.timer.hasReached(500)) return;

        if (!(mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiContainerCreative)) {
            if (openValue.getValue()) {
                return;
            }
        }
        if (mc.player.openContainer.windowId != 0) {
            startTimer.reset();
            return;
        }

        if (!startTimer.hasReached(startDelayValue.getValue().longValue())) return;
        if (MovementUtil.isMoving() && noMoveValue.getValue()) return;
        if (mc.player.isHandActive() && noUsingValue.getValue()) return;
        if (mc.player.isHandActive() && NoSlow.shouldSlow) return;
        if (!timer.hasReached(delay)) return;
        if (waitingResponse && !rightClickTimer.hasReached(300)) return;
        delay = (long) MathUtil.getRandomInRange(minDelayValue.getValue().longValue(), maxDelayValue.getValue().longValue());

        itemSlot.clear();

        for (int i = 0; i < slotValue.size(); i++) {
            String item = slotValue.get(i).getMode();
            if (item.equals("None")) continue;
            itemSlot.put(item, i + 36);
        }

        for (SubComponent component : subComponents) {
            if (!itemSlot.containsKey(component.name)) continue;
            if (component.sort()) {
                if (!timer.hasReached(delay)) return;
            }
        }

        if (armorValue.getValue()) {
            for (ArmorComponent armorComponent : armorComponents) {
                if (armorComponent.sort()) {
                    if (!timer.hasReached(delay)) return;
                }
            }
        }

        for (int i = 9; i < 45; i++) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);
            if (!slot.getHasStack()) continue;
            boolean match = false;
            for (SubComponent component : subComponents) {
                if (component.match(slot.getStack())) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                ItemStack itemStack = slot.getStack();
                Item item = itemStack.getItem();
                if (item instanceof ItemArrow && arrowValue.getValue()) continue;
                if ((item instanceof ItemEgg || item instanceof ItemSnowball) && throwableValue.getValue()) continue;
                if (bedWarsValue.getValue() && bedWarsItem.contains(item)) continue;
                if (itemStack.getDisplayName().matches(serverItems) && serverValue.getValue()) continue;
                if (InventoryUtil.isSharpAxe(itemStack) || InventoryUtil.isKnockBackSlimeball(itemStack)) continue;
                this.drop(i);
                if (!timer.hasReached(delay)) return;
            }
        }
    }

    public boolean match(ItemStack stack) {
        if (stack.isEmpty()) return false;

        Item item = stack.getItem();

        if (item instanceof ItemArmor) {
            for (ArmorComponent component : armorComponents) {
                if (component.match(stack)) {
                    return component.choose(mc.player.inventory.getStackInSlot(component.getArmorSlot()), stack) == stack;
                }
            }
        } else {
            for (SubComponent component : subComponents) {
                if (component.match(stack)) {
                    if (!component.throwInferior()) return true;
                    return component.choose(component.getBest(), stack) == stack;
                }
            }
        }

        if (item instanceof ItemArrow && arrowValue.getValue()) return true;
        if ((item instanceof ItemEgg || item instanceof ItemSnowball) && throwableValue.getValue()) return true;
        if (bedWarsValue.getValue() && bedWarsItem.contains(item)) return true;
        if (stack.getDisplayName().matches(serverItems) && serverValue.getValue()) return true;
        if (InventoryUtil.isSharpAxe(stack) || InventoryUtil.isKnockBackSlimeball(stack)) return true;

        return false;
    }

    public HashMap<String, Integer> getItemSlot() {
        return itemSlot;
    }

    public Dropdown getItemValue(String name, String defaultItem) {
        return new Dropdown(name, defaultItem, "Weapon", "Block", "Axe", "Pickaxe", "Bow", "Gapple", "Food", "Pearl", "Potion", "Projectile", "TNT", "None");
    }

    public static boolean throwInferior() {
        return !INSTANCE.inferiorValue.getValue();
    }

    public static boolean throwDebuff() {
        return !INSTANCE.debuffValue.getValue();
    }
}
