package dev.diona.southside.module;

import com.google.gson.JsonElement;
import dev.diona.southside.Southside;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

public abstract class Value<T> implements Serializable {
    private String name;
    private T value, defaultValue;
    private BooleanSupplier display;

    protected Value(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplay(BooleanSupplier display) {
        this.display = display;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.setValueNoSave(value);
    }

    public void setValueNoSave(T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isHidden() {
        return display != null && !display.getAsBoolean();
    }
}