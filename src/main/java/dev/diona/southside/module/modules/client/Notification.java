package dev.diona.southside.module.modules.client;

import cc.polyfrost.oneconfig.gui.animations.Animation;
import cc.polyfrost.oneconfig.gui.animations.DummyAnimation;
import cc.polyfrost.oneconfig.gui.animations.EaseInOutQuad;
import cc.polyfrost.oneconfig.internal.assets.SVGs;
import cc.polyfrost.oneconfig.renderer.asset.Icon;
import cc.polyfrost.oneconfig.utils.Notifications;
import cc.polyfrost.oneconfig.utils.gui.GuiUtils;
import dev.diona.southside.Southside;
import dev.diona.southside.event.EventState;
import dev.diona.southside.event.events.Bloom2DEvent;
import dev.diona.southside.gui.style.ClientStyle;
import dev.diona.southside.module.Category;
import dev.diona.southside.module.Module;
import dev.diona.southside.module.annotations.DefaultEnabled;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.util.misc.MathUtil;
import dev.diona.southside.util.misc.TimerUtil;
import dev.diona.southside.util.misc.BezierUtil;
import dev.diona.southside.util.player.ChatUtil;
import dev.diona.southside.util.render.RenderUtil;
import dev.diona.southside.util.render.RoundUtil;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static dev.diona.southside.Southside.MC.mc;

@DefaultEnabled
public class Notification extends Module {
    public enum NotificationType {
        INFO, WARN, ERROR, ENABLED, DISABLED
    }

    private static class NotificationMessage {
        public String message;
        NotificationType type;
        public BezierUtil x = new BezierUtil(3, 0), y = new BezierUtil(3, 0);
        public long time, keepTime;
        public boolean initialized;
        public String title;

        public NotificationMessage(String message, String title, NotificationType type, double keepTime) {
            this.message = message;
            this.title = title;
            this.type = type;
            this.time = (long) (keepTime * 1000);
            this.keepTime = (long) (keepTime * 1000);
            this.initialized = false;
        }
    }

    private static Notification INSTANCE;

    public final Slider keepTimeValue = new Slider("Keep Time", 1, 0, 5, 0.1);
    private static final List<NotificationMessage> messages = new CopyOnWriteArrayList<>();

    private static final float gap = 10;

    private static final TimerUtil timer = new TimerUtil();
    public Notification(String name, String description, Category category, boolean visible) {
        super(name, description, category, visible);
        INSTANCE = this;
    }

    @Override
    public boolean onDisable() {
        messages.clear();
        return true;
    }

    @EventListener
    public void onBloom(Bloom2DEvent event) {
        synchronized (messages) {

            ScaledResolution sr = event.getSr();
            float srwidth = sr.getScaledWidth();
            float srheight = sr.getScaledHeight();
            float height = 35, minWidth = 140, extraWidth = 40, offsetX = 5, offsetY = -5;
            float messageY = srheight - height + offsetY;
            float fontSize = 12F;

            if (event.getState() == EventState.PRE) {
                for (NotificationMessage message : messages) {
                    message.time -= timer.passed();
                }
                timer.reset();

                for (int i = 0; i < messages.size(); i++) {
                    NotificationMessage message = messages.get(i);


                    float width = Math.max(minWidth, Southside.fontManager.font.getStringWidth(fontSize, message.message) + extraWidth);
                    if (!message.initialized) {
                        message.x.set(srwidth + offsetY);
                        message.y.set(messageY);
                        message.initialized = true;
                    }

                    if (message.time < 0) {
                        message.x.update(srwidth + 10);
                    } else {
                        message.x.update(srwidth - width + offsetX);
                    }

                    if (message.x.get() > srwidth + 0.5f) {
                        messages.remove(i);
                        i--;
                    }

                    message.y.update(messageY);
                    if (message.x.get() < srwidth - width / 2) {
                        messageY -= height + gap;
                    }

                    message.x.freeze();
                    message.y.freeze();
                }
            }

            ClientStyle clientStyle = Southside.styleManager.getStyle();
            for (NotificationMessage message : messages) {
                float width = Math.max(minWidth, Southside.fontManager.font.getStringWidth(fontSize, message.message) + extraWidth);
                Color color = Color.BLACK;
                switch (message.type) {
                    case INFO:
                        color = clientStyle.getHudNotificationInfoColor();
                        break;
                    case WARN:
                        color = clientStyle.getHudNotificationWarningColor();
                        break;
                    case ERROR:
                        color = clientStyle.getHudNotificationErrorColor();
                        break;
                    case ENABLED:
                        color = clientStyle.getHudNotificationEnabledColor();
                        break;
                    case DISABLED:
                        color = clientStyle.getHudNotificationDisabledColor();
                        break;
                }
                double percent = 1 - MathUtil.clamp(((double) message.time) / message.keepTime, 0d, 1d);
                RoundUtil.drawRound(message.x.get(), message.y.get(), width, height, 3, false, new Color(32, 32, 32));
                RenderUtil.scissorStart(message.x.get() - 10F, message.y.get() + height - 3F, (float) (width * percent) + 10F, 20F);
                RoundUtil.drawRound(message.x.get(), message.y.get(), width, height, 3, false, color);
                RenderUtil.scissorEnd();

                Color fontColor = event.getState() == EventState.PRE ? clientStyle.getFontShadowColor() : clientStyle.getHudNotificationTextColor();

                Southside.fontManager.font.drawString(9F, message.title, message.x.get() + 6, message.y.get() + 4, fontColor);
                Southside.fontManager.font.drawString(fontSize, message.message, message.x.get() + 6, message.y.get() + height - Southside.fontManager.font.getHeight(fontSize) * 1.7F, fontColor);
            }
        }
    }

    public static void addNotification(String message, String title, NotificationType type) {
        if (mc.world == null) return;
        addNotificationKeepTime(message, title, type, INSTANCE.keepTimeValue.getValue().doubleValue());
    }

    public static void addNotificationKeepTime(String message, String title, NotificationType type, double keepTime) {
        if (mc.world == null) return;
        long time = System.currentTimeMillis();
        Animation animation = new EaseInOutQuad((int) (keepTime * 1000F), 0, 1, false);
        Notifications.INSTANCE.send(
                title,
                message,
                getIcon(type),
                (float) (keepTime * 1000),
                () -> animation.get(GuiUtils.getDeltaTime())
        );
//        messages.add(new NotificationMessage(message, title, type, keepTime));
    }

    public static final Icon ENABLED_ICON = new Icon(SVGs.NOTIFICATION_ENABLED);
    public static final Icon DISABLED_ICON = new Icon(SVGs.NOTIFICATION_DISABLED);
    public static final Icon INFO_ICON = new Icon(SVGs.INFO_ARROW);
    public static final Icon WARNING_ICON = new Icon(SVGs.WARNING);
    public static final Icon ERROR_ICON = new Icon(SVGs.ERROR);

    private static Icon getIcon(NotificationType type) {
        return switch (type) {
            case ENABLED -> ENABLED_ICON;
            case DISABLED -> DISABLED_ICON;
            case INFO -> INFO_ICON;
            case WARN -> WARNING_ICON;
            case ERROR -> ERROR_ICON;
        };
    }
}