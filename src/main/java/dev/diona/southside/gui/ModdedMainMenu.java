package dev.diona.southside.gui;

//import dev.diona.southside.gui.alt.GuiAccountManager;

import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.asset.Image;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import dev.diona.southside.Southside;
import dev.diona.southside.util.render.RenderUtil;
import me.sunstorm.blaze.Animation;
import me.sunstorm.blaze.AnimationType;
import me.sunstorm.blaze.Animator;
import me.sunstorm.blaze.Eases;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjglx.input.Keyboard;

import java.awt.Color;
import java.io.IOException;

public class ModdedMainMenu extends GuiScreen {
    private long initTime = System.currentTimeMillis();
    private final Animator animator = new Animator();
    private final Animation fade;
    private int currentBackground = 0;
    private final Image[] backgrounds;
    public ModdedMainMenu() {
        fade = Animation.animation(Eases.LINEAR, AnimationType.bouncing(Eases.LINEAR), 0.05);
        animator.start(fade);
        backgrounds = new Image[]{
                new Image("/assets/minecraft/southside/main_menu/background1.jpg"),
                new Image("/assets/minecraft/southside/main_menu/background2.jpg")
        };
    }

    @Override
    public void initGui() {
        final int j = this.height / 4 + 10;
        this.addSingleplayerMultiplayerButtons(j, 20);
        super.initGui();
        this.initTime = System.currentTimeMillis();
    }

    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_)
    {
        final int objHeight = 17;
        final int objWidth = 63;
        int i = this.width / 2 - 150 / 2;
        this.buttonList.add(new GuiMenuButton(1, i, p_73969_1_ + p_73969_2_, objWidth ,objHeight, I18n.format("menu.singleplayer"), "singleplayer"));
        this.buttonList.add(new GuiMenuButton(2, i, p_73969_1_ + p_73969_2_ * 3, objWidth ,objHeight, I18n.format("menu.multiplayer"), "multiplayer"));
        /*
        final var altManager = new GuiMenuButton(3, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, objWidth ,objHeight, "Alt Manager");
        altManager.enabled = false;
        this.buttonList.add(altManager);

         */
        this.buttonList.add(new GuiMenuButton(0, i, p_73969_1_ + p_73969_2_ * 5, objWidth ,objHeight, I18n.format("menu.options"), "settings"));
        this.buttonList.add(new GuiMenuButton(4, i, p_73969_1_ + p_73969_2_  * 7, objWidth ,objHeight, I18n.format("menu.quit"), "exit"));
    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 1:
                this.mc.displayGuiScreen(new GuiWorldSelection(this));
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 3:
//                this.mc.displayGuiScreen(new GuiAccountManager(this));
                break;
            case 4:
                this.mc.shutdown();
                break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_LEFT) {
            if ((currentBackground) <= 0) {
                currentBackground = backgrounds.length - 1;
            } else {
                currentBackground--;
            }
        } else if (keyCode == Keyboard.KEY_RIGHT) {
            if ((currentBackground + 1) >= backgrounds.length) {
                currentBackground = 0;
            } else {
                currentBackground++;
            }

        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        animator.update(partialTicks);
        NanoVGHelper nanovg = NanoVGHelper.INSTANCE;
        final var scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        var value = fade.value();
        if (value <= 0.05) value = 0.05;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        double finalValue = value;
        nanovg.setupAndDraw(true, vg -> {
            nanovg.drawImage(vg, backgrounds[currentBackground], 0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
            nanovg.drawText(
                    vg,Southside.CLIENT_NAME,
                    (scaledResolution.getScaledWidth() / 2f) - (nanovg.getTextWidth(vg, Southside.CLIENT_NAME, 50, Fonts.BOLD) / 2f),
                    80.0F,
                    new Color(255, 255, 255, (int) (255 * finalValue)).getRGB(),
                    50,
                    Fonts.BOLD
            );
        });
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
