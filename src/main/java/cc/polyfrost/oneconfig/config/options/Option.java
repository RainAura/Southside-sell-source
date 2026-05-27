package cc.polyfrost.oneconfig.config.options;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.OptionType;
import cc.polyfrost.oneconfig.config.elements.BasicOption;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class Option<V> implements IOption {

    private final String label;

    private final V defaultValue;

    public V value;
    public transient BasicOption basicOption = null;

    public Option(@NotNull String label, @NotNull V defaultValue) {
        this.label = Objects.requireNonNull(label);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.reset();
    }

    public String getLabel() {
        return label;
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public void setValue(V value) {
        this.value = value;
        if (basicOption != null) {
            basicOption.triggerListeners();
        }
    }

    public V getValue() {
        return value;
    }

    public void reset() {
        try {
            value = defaultValue;
            Config.parseConfig(Config.saveConfig(new JsonObject(), this), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        if (defaultValue instanceof OneKeyBind defaultKeyBind) {
//            ((OneKeyBind) value).setRunnable(defaultKeyBind.getRunnable());
//        }
    }
}
