package dev.diona.southside.util.misc;

import com.mojang.authlib.GameProfile;
import dev.diona.southside.module.modules.player.BalancedTimer;
import dev.diona.southside.module.modules.player.Blink;
import dev.diona.southside.util.player.ChatUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static dev.diona.southside.Southside.MC.mc;

public class FakePlayer extends EntityOtherPlayerMP {
    private final EntityPlayer player;
    public static int idIndex = 0;

    public FakePlayer(EntityPlayer player) {
        super(mc.world, player.getGameProfile());
        this.player = player;

        this.getDataManager().setEntryValues(Objects.requireNonNull(player.getDataManager().getAll()));
        this.copyLocationAndAnglesFrom(player);
//        this.inventory.copyInventory(player.inventory);
        this.setHealth(player.getHealth());
        this.setAbsorptionAmount(player.getAbsorptionAmount());

        this.setPositionAndRotation(
                player.posX,
                player.posY,
                player.posZ,
                player.rotationYaw,
                player.rotationPitch
        );

        this.rotationYaw = player.rotationYaw;
        this.rotationPitch = player.rotationPitch;
        this.rotationYawHead = player.rotationYawHead;

        mc.world.addEntityToWorld(--idIndex, this);

        if (idIndex <= -100000) {
            idIndex = -1;
        }
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return this.isInvisible();
    }

    @Override
    public boolean isInvisible() {
        if (this == Blink.fakePlayer || this == dev.diona.southside.module.modules.misc.FakePlayer.fakePlayer) {
            return false;
        }
        return mc.scheduledTasks.size() <= 3;
    }

    @Override
    public void onUpdate() {
//        if (mc.scheduledTasks.size() <= 3 && this.getDistanceSq(player) >= 10) {
//            player.fakePlayer = new FakePlayer(player);
//            mc.world.removeEntity(this);
//        }
        if (player == null || !player.isEntityAlive()) {
            mc.world.removeEntity(this);
        }
        this.setSprinting(false);
        super.onUpdate();
    }
}
