package dev.diona.southside.module;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.options.impl.KeyBind;
import cc.polyfrost.oneconfig.gui.OneConfigGui;
import cc.polyfrost.oneconfig.platform.Platform;
import cc.polyfrost.oneconfig.utils.Notifications;
import cc.polyfrost.oneconfig.utils.TickDelay;
import dev.diona.southside.module.Category;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.diona.southside.Southside;
import dev.diona.southside.module.annotations.Binding;
import dev.diona.southside.module.annotations.DefaultEnabled;
import dev.diona.southside.module.modules.client.Notification;
import cc.polyfrost.oneconfig.config.options.impl.Slider;
import dev.diona.southside.module.modules.player.Stealer;
import net.minecraft.util.text.TextFormatting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Module extends Config implements BaseModule, Southside.MC {
    protected Runnable initPostRunnable;
    private String name, description;

    public KeyBind keyBind = new KeyBind(
            "Bind",
            "",
            1,
            new OneKeyBind(this.getDefaultBind())
        );

    private Category category;
    private final List<Value<?>> values = new ArrayList<>();
    private boolean visible;
    public boolean openValues;

    public Module(String name, String description, Category category, boolean visible) {
        super(new Mod(name, category), name + ".json");
        this.name = name;
        this.description = description;
        this.category = category;
        this.visible = visible;
//        this.setEnableNoSave(this.isDefaultEnabled());
        this.enabled.setValue(this.isDefaultEnabled());
        this.enabled.setDefaultValue(this.isDefaultEnabled());
        if (this.isDefaultEnabled()) {
            Southside.eventBus.subscribe(this);
        }
//        initialize(); // <--- 类的构造函数执行调用父类构造函数时，模块的value还没进行初始化进行赋值
                        //      所以这样写会拿不到东西
    }

    @Override
    public void initialize() {
        super.initialize();
        //            new TickDelay(() -> this.toggle(), 1);
        registerKeyBind(keyBind.getValue(), () -> {
            if (mc.currentScreen == null || Stealer.isSilentStealing()) {
                this.toggle();
            }
        });
        keyBind.getDefaultValue().setRunnable(keyBind.getValue().getRunnable());
//        registerKeyBind(keyBind.getDefaultValue(), () -> {
//            if (mc.currentScreen == null || Stealer.isSilentStealing()) {
//                this.toggle();
//            }
//        });
    }

    public boolean onEnable() {
        return true;
    }

    public boolean onDisable() {
        return true;
    }

    public boolean isEnabled() {
        return enabled.getValue();
    }

    public void setEnable(boolean enabled) {
        if (enabled == this.enabled.getValue()) return;
        this.setEnableNoSave(enabled);
        this.save();
    }

    public void setEnableNoSave(boolean enabled) {
        if (enabled) {
            Notification.addNotification("Toggled " + name + " on.", "Module", Notification.NotificationType.ENABLED);
            this.enabled.setValue(true);
            if (!this.onEnable()) {
                this.enabled.setValue(false);
                return;
            }
//            toggleListener.forEach((e)->e.accept(this.enabled.getValue()));
            Southside.eventBus.subscribe(this);
        } else {
            Notification.addNotification("Toggled " + name + " off.", "Module", Notification.NotificationType.DISABLED);
            this.enabled.setValue(false);
            if (!this.onDisable()) {
                this.enabled.setValue(true);
                return;
            }
            Southside.eventBus.unsubscribe(this);
        }

    }

    public void toggle() {
        this.setEnable(!this.enabled.getValue());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getSuffix() {
        return "";
    }

    public List<Value<?>> getValues() {
        return values;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private boolean isDefaultEnabled() {
        return this.getClass().isAnnotationPresent(DefaultEnabled.class);
    }

    public int getDefaultBind() {
        if (this.getClass().isAnnotationPresent(Binding.class)) {
            return this.getClass().getAnnotation(Binding.class).value();
        } else {
            return 0;
        }
    }

    public String formattedName() {
        return this.getSuffix().isEmpty() ? this.getName() : String.format("%s %s%s", this.getName(), TextFormatting.GRAY, this.getSuffix());
    }

    public void initPostRunnable() {
        if (initPostRunnable != null)
            initPostRunnable.run();
    }

    public void addRangedValueRestrict(Slider min, Slider max) {
        addListener(min.getLabel(), () -> {
            if (min.getValue().floatValue() > max.getValue().floatValue()) {
                min.setValue(max.getValue());
            }
        });
        addListener(max.getLabel(), () -> {
            if (max.getValue().floatValue() < min.getValue().floatValue()) {
                max.setValue(min.getValue());
            }
        });
    }
}
