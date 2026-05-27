package dev.diona.southside.gui.container;

import dev.diona.southside.util.quickmacro.metadatas.VexViewMetadata;
import dev.diona.southside.util.player.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;

import static dev.diona.southside.Southside.MC;

public class HytPartyInputGui extends GuiEditSign {
    private String field;
    private VexViewMetadata confirm;

    String prefix;

    public HytPartyInputGui(String field, VexViewMetadata confirm, String prefix) {
        super(getSign());
        this.field = field;
        this.confirm = confirm;
        this.prefix = prefix;
    }

    public static TileEntitySign getSign() {
        TileEntitySign teSign = new TileEntitySign();
        teSign.setWorld(MC.mc.world);
        teSign.setPos(BlockPos.ORIGIN);
//        teSign.signText[0] = new ChatComponentText("请输入玩家 ID：");
        return teSign;
    }

    @Override
    public void onGuiClosed() {
//        VexViewSender.joinParty(this.tileSign.signText[0].getFormattedText().trim(), field, confirm.id);
        mc.player.sendChatMessage(prefix + this.tileSign.signText[0].getUnformattedText().trim());
        ChatUtil.sendText(prefix);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "请输入玩家 ID", this.width / 2, 40, 16777215);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(this.width / 2), 0.0F, 50.0F);
        float f = 93.75F;
        GlStateManager.scale(-f, -f, -f);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        Block block = this.tileSign.getBlockType();

        if (block == Blocks.STANDING_SIGN)
        {
            float f1 = (float)(this.tileSign.getBlockMetadata() * 360) / 16.0F;
            GlStateManager.rotate(f1, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, -1.0625F, 0.0F);
        }
        else
        {
            int i = this.tileSign.getBlockMetadata();
            float f2 = 0.0F;

            if (i == 2)
            {
                f2 = 180.0F;
            }

            if (i == 4)
            {
                f2 = 90.0F;
            }

            if (i == 5)
            {
                f2 = -90.0F;
            }

            GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, -1.0625F, 0.0F);
        }

        if (this.updateCounter / 6 % 2 == 0)
        {
            this.tileSign.lineBeingEdited = this.editLine;
        }

        TileEntityRendererDispatcher.instance.render(this.tileSign, -0.5D, -0.75D, -0.5D, 0.0F);
        this.tileSign.lineBeingEdited = -1;
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
