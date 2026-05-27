package cc.polyfrost.oneconfig.config.options;

import cc.polyfrost.oneconfig.config.Config;
import com.google.gson.JsonObject;

public class WrappedValue<V> {

    private final String label;
    private final Class<?> clz;
    private V value;
    private V defaultValue;

    public WrappedValue(String label, V value) {
        this.clz = value.getClass();
        this.label = label;
        this.defaultValue = value;
        setValue(value);
        this.reset();
    }

    public void setDefaultValue(V defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Class<?> getTargetClass() {
        return clz;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public void reset() {
        try {
            Config.parseConfig(Config.saveConfig(new JsonObject(), this), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
