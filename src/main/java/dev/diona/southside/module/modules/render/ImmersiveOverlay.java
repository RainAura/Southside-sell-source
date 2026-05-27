package dev.diona.southside.module.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dev.diona.southside.event.events.Render2DEvent;
import dev.diona.southside.event.events.RenderInGameEvent;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.util.render.RenderUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ImmersiveOverlay extends Module {
    public ImmersiveOverlay(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
    }

    @EventListener
    public final void onRender2DEvent(final Render2DEvent event) {
        var w =  event.getSr().getScaledWidth_double() / 2;
        var j1 = event.getSr().getScaledHeight_double() - 15;
        final float food_progress = Minecraft.getMinecraft().player.getFoodStats().getFoodLevel() / 20.0f;
        RenderUtil.drawGradientRectBordered(w - 5, event.getSr().getScaledHeight_double() - 17, (w - 5) + 99, event.getSr().getScaledHeight_double() - 2, 0.5,  new Color(0,0,0,0).getRGB(),  new Color(0,0,0,0).getRGB(), new Color(139,69,19, 150).getRGB(), new Color(139,69,19, 150).getRGB());
        RenderUtil.drawGradientRectBordered(w - 5, event.getSr().getScaledHeight_double() - 17, (w - 5) + (99 * food_progress), event.getSr().getScaledHeight_double() - 2, 0.5,  new Color(139,69,19, 200).getRGB(),  new Color(139,69,19, 150).getRGB(), new Color(0,0,0,0).getRGB(), new Color(0,0,0,0).getRGB());
        List<ItemStack> stuff = new ArrayList<>();
        ItemStack errything;
        for(int index = 0; index <= 3; ++index) {
           errything = Minecraft.getMinecraft().player.inventory.armorInventory.get(index);
           if (errything != null) {
              stuff.add(errything);
           }
        }
        int split = -3;
        for(var e : stuff) {
            int i111 = event.getSr().getScaledWidth() / 2 - 30 - split;
            int j111 = event.getSr().getScaledHeight() - 18;
            RenderUtil.drawGradientRectBordered(i111, j111, i111 + 16, j111 + 17, 1, new Color(0,0,0,0).getRGB(), new Color(0,0,0,0).getRGB(), -1, -1);
            this.renderHotbarItem(i111, j111, event.getPartialTicks(), Minecraft.getMinecraft().player, e);
            split += 20;
        }
        RenderUtil.drawGradientRectBordered(w - 99.5, j1 - 19.5, w + 99,  j1 - 4.5, 0.5, new Color(0,0,0,0).getRGB(), new Color(0,0,0,0).getRGB(), -1, -1);
        float progress = Minecraft.getMinecraft().player.getHealth() / Minecraft.getMinecraft().player.getMaxHealth();
        RenderUtil.drawGradientRectBordered(w - 100, j1 - 20, (w - 100) + (200 * progress),  j1 - 4, 1, new Color(255,255,255,150).getRGB(), new Color(255,255,255,150).getRGB(), new Color(0,0,0,0).getRGB(), new Color(0,0,0,0).getRGB());

        RenderUtil.drawRect(3, 3, 23, 16, new Color(0,0,0,150).getRGB());
        RenderUtil.drawRect(23, 3, 40, 16, new Color(0,0,0,80).getRGB());
        Minecraft.getMinecraft().fontRenderer.drawString("剩余", 5, 5, -1);
        final var playerCount = Minecraft.getMinecraft().world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).count();
        Minecraft.getMinecraft().fontRenderer.drawString(String.valueOf(playerCount), 27, 5, -1);
        
        RenderUtil.drawRect(50, 3, 70, 16, new Color(0,0,0,150).getRGB());
        RenderUtil.drawRect(70, 3, 90, 16, new Color(0,0,0,80).getRGB());
        Minecraft.getMinecraft().fontRenderer.drawString("淘汰", 52, 5, -1);
        Minecraft.getMinecraft().fontRenderer.drawString("1", 75, 5, -1);
        
        RenderUtil.drawRect(3, 20, 20, 37, new Color(150,150,0).getRGB());
        Minecraft.getMinecraft().fontRenderer.drawString("1", 9, 25, -1);
        RenderUtil.drawRect(20, 20, 91, 37, new Color(0, 0, 0, 80).getRGB());
        Minecraft.getMinecraft().fontRenderer.drawString("Player296", 25, 25, -1);
        RenderUtil.drawRect(20, 36, 91, 37, -1);
    }
    
    @EventListener
    public void onRenderHUDEvent(final RenderInGameEvent event) {
        switch (event.type) {
            case Hotbar -> {
                event.setCancelled(true);
                List<ItemStack> stuff = new ArrayList<>();
                ItemStack errything;
                for(int index = 8; index >= 0; --index) {
                   errything = Minecraft.getMinecraft().player.inventory.mainInventory.get(index);
                   if (errything != null) {
                      stuff.add(errything);
                   }
                }
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                RenderHelper.enableGUIStandardItemLighting();
                for (int i = 0; i < stuff.size(); i++) {
                	var item = stuff.get(i);
                	var text_w = Minecraft.getMinecraft().fontRenderer.getStringWidth(item.getDisplayName());
                    int i1 = event.scaledResolution.getScaledWidth() - 20;
                    int j1 = event.scaledResolution.getScaledHeight() - 20 - (i * 16);
                    if (item == Minecraft.getMinecraft().player.inventory.getCurrentItem()) {
    						RenderUtil.drawGradientRectBordered(i1 - text_w - 10, j1, i1 + 20, j1 + 18, 1, new Color(255,255,255, 0).getRGB(), new Color(255,255,255, 200).getRGB(), 0, 0);
    					
    				}
                    if (item.getItem() == Items.AIR) {
						continue;
					}
                    Minecraft.getMinecraft().fontRenderer.drawString(item.getDisplayName(),i1 - text_w, j1 + 5, -1);
                    Minecraft.getMinecraft().fontRenderer.drawString(String.valueOf(item.getCount()) ,i1 - text_w - Minecraft.getMinecraft().fontRenderer.getStringWidth(String.valueOf(item.getCount())) - 5, j1 + 5, -1);
                    this.renderHotbarItem(i1, j1, event.partialTicks, Minecraft.getMinecraft().player, item);
				}
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
            }		
            case ExpBar -> {
                event.setCancelled(true);
            }
            case PlayerStats -> {
                event.setCancelled(true);
            }
		default -> throw new IllegalArgumentException("Unexpected value: " + event.type);
        }
    }
    
    private void renderHotbarItem(int x, int y, float partialTicks, EntityPlayer player, ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            float f = (float)stack.getAnimationsToGo() - partialTicks;

            if (f > 0.0F)
            {
                GlStateManager.pushMatrix();
                float f1 = 1.0F + f / 5.0F;
                GlStateManager.translate((float)(x + 8), (float)(y + 12), 0.0F);
                GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
            }

            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(player, stack, x, y);

            if (f > 0.0F)
            {
                GlStateManager.popMatrix();
            }

            //Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, stack, x, y);
        }
    }
}
