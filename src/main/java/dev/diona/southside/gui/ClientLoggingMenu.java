package dev.diona.southside.gui;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.events.event.ShutdownEvent;
import cc.polyfrost.oneconfig.events.event.StartEvent;
import cc.polyfrost.oneconfig.internal.OneConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.diona.southside.Southside;
import dev.diona.southside.util.authentication.AuthenticatedUser;
import dev.diona.southside.util.authentication.AuthenticationStatus;
import dev.diona.southside.util.authentication.WebUtil;
import dev.diona.southside.util.chat.Chat;
import me.sunstorm.blaze.Animation;
import me.sunstorm.blaze.AnimationType;
import me.sunstorm.blaze.Animator;
import me.sunstorm.blaze.Eases;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.optifine.CustomPanorama;
import net.optifine.CustomPanoramaProperties;
import org.lwjgl.opengl.GL11;
import org.lwjglx.util.glu.Project;
import top.fl0wowp4rty.phantomshield.api.User;

import java.awt.*;
import java.util.HashMap;

public final class ClientLoggingMenu extends GuiScreen {
    private static final ResourceLocation WINDOW = new ResourceLocation("textures/gui/advancements/window.png");
    private float panoramaTimer;
    private static final ResourceLocation[] TITLE_PANORAMA_PATHS = new ResourceLocation[] {new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
    private final Animator animator = new Animator();
    private final Animation fade;
    public String authStatusText = "Idle...";
    public static final Gson gson = new GsonBuilder().create();
    public ClientLoggingMenu() {
        fade = Animation.animation(Eases.LINEAR, AnimationType.bouncing(Eases.LINEAR), 0.07);
        animator.start(fade);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        animator.update(partialTicks);
        this.panoramaTimer += partialTicks;
        drawPanorama(mouseX, mouseY, partialTicks);
        final var resolution = new ScaledResolution(Minecraft.getMinecraft());
        final var x = resolution.getScaledWidth_double() / 2 - this.fontRenderer.getStringWidth(authStatusText) * 3 / 2;
        final var y = resolution.getScaledHeight() / 2 - 15;
        var value = fade.value();
        if (value <= 0.05) value = 0.05;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0.0d);
        GL11.glScaled(3d, 3d, 3d);
        GL11.glEnable(GL11.GL_BLEND);
        this.fontRenderer.drawStringWithShadow(authStatusText, 0,0, new Color(255, 255, 255, (int) (255 * value)).getRGB());
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        initialize(new String(User.INSTANCE.getUserData()));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (canLaunch) {
            canLaunch = false;
            Southside.start();
            Minecraft.getMinecraft().displayGuiScreen(new ModdedMainMenu());

            EventManager.INSTANCE.post(new StartEvent());
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> EventManager.INSTANCE.post(new ShutdownEvent())));

            EventManager.INSTANCE.post(new InitializationEvent());
            OneConfig.INSTANCE.init();

            Southside.moduleManager.toggleAllListeners();
        }

    }

    static volatile boolean isInitialized = false;

    static volatile boolean canLaunch = false;

    public synchronized void initialize(final String token) {
        if (Southside.type == Southside.ClientType.BETA) {
            if (User.INSTANCE.getExpiredDate()[0] <= 2030) {
                authStatusText = "You do not have permission to access the beta version.";
                return;
            }
        }

        if (!isInitialized) {
            isInitialized = true;
            canLaunch = true;
            AuthenticationStatus.INSTANCE.user = new AuthenticatedUser("Dev");
            AuthenticationStatus.INSTANCE.magic = 600000;
            if (true) return;

            authStatusText = "Fetching Information...";
            WebUtil.api("/compatible/phantomshield/upgradeToken", new WebUtil.CallbackWithParams<>() {
                @Override
                public HashMap<String, String> params(HashMap<String, String> params) {
                    params.put("token", token);
                    params.put("name", User.INSTANCE.getUsername(null));
                    return params;
                }

                @Override
                public void onSuccess(JsonObject e) {
                    if (e.get("code").getAsInt() != 0) {
                        authStatusText = e.get("msg").getAsString();
                        return;
                    }
                    final var upgradedToken = e.get("data").getAsString();

                    WebUtil.api("/user/me", new WebUtil.CallbackWithParams<>() {
                        @Override
                        public HashMap<String, String> params(HashMap<String, String> params) {
                            params.put("token", upgradedToken);
                            return params;
                        }

                        @Override
                        public void onSuccess(JsonObject e) {
                            if (e.get("code").getAsInt() == 0) {
                                final var username = e.get("data").getAsJsonObject().get("Name").getAsString();
                                AuthenticationStatus.INSTANCE.user = new AuthenticatedUser(username);
                                AuthenticationStatus.INSTANCE.magic = 1000000;
                                AuthenticationStatus.INSTANCE.token = upgradedToken;
                                authStatusText = "Allocating Session...";
                                WebUtil.api("/session/new", new WebUtil.CallbackWithParams<>() {
                                    @Override
                                    public HashMap<String, String> params(HashMap<String, String> params) {
                                        params.put("token", upgradedToken);
                                        return params;
                                    }

                                    @Override
                                    public void onSuccess(JsonObject e) {
                                        if (e.get("code").getAsInt() == 0) {
                                            AuthenticationStatus.INSTANCE.session = e.get("data").getAsString();
                                            Chat.getInstance();
                                            authStatusText = "Initialize...";
                                            canLaunch = true;
                                        } else {
                                            authStatusText = TextFormatting.RED + e.get("msg").getAsString();
                                        }
                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        authStatusText = TextFormatting.RED + e.getMessage();
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                authStatusText = TextFormatting.RED + e.get("msg").getAsString();
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            authStatusText = TextFormatting.RED + e.getMessage();
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                public void onFail(Exception e) {
                    authStatusText = TextFormatting.RED + e.getMessage();
                    e.printStackTrace();
                }
            });
            isInitialized = true;
        }
    }

    private void drawPanorama(int mouseX, int mouseY, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        //GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int i = 8;
        int j = 64;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if (custompanoramaproperties != null) {
            j = custompanoramaproperties.getBlur1();
        }

        for (int k = 0; k < j; ++k) {
            GlStateManager.pushMatrix();
            float f = ((float) (k % 8) / 8.0F - 0.5F) / 64.0F;
            float f1 = ((float) (k / 8) / 8.0F - 0.5F) / 64.0F;
            float f2 = 0.0F;
            GlStateManager.translate(f, f1, 0.0F);
            GlStateManager.rotate(MathHelper.sin(this.panoramaTimer / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-this.panoramaTimer * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int l = 0; l < 6; ++l) {
                GlStateManager.pushMatrix();

                if (l == 1) {
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 3) {
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 4) {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (l == 5) {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                ResourceLocation[] aresourcelocation = TITLE_PANORAMA_PATHS;

                if (custompanoramaproperties != null) {
                    aresourcelocation = custompanoramaproperties.getPanoramaLocations();
                }

                this.mc.getTextureManager().bindTexture(aresourcelocation[l]);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int i1 = 255 / (k + 1);
                float f3 = 0.0F;
                bufferbuilder.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, i1).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, i1).endVertex();
                bufferbuilder.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, i1).endVertex();
                bufferbuilder.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, i1).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }
}
