package cc.polyfrost.oneconfig.config.options.impl;

import cc.polyfrost.oneconfig.config.data.OptionType;
import cc.polyfrost.oneconfig.config.options.Option;

public class Switch extends Option<Boolean> {

    private final String description;
    private final int size;

    public Switch(String label, String description, int size, boolean defaultValue) {
        super(label, defaultValue);
        this.description = description == null ? "" : description;
        this.size = size;
    }

    public Switch(String label, String description, boolean defaultValue) {
        this(label, description, 1, defaultValue);
    }

    public Switch(String label, boolean defaultValue) {
        this(label, "", 1, defaultValue);
    }

    @Override
    public OptionType type() {
        return OptionType.SWITCH;
    }

    public String getDescription() {
        return description;
    }

    public int getSize() {
        return size;
    }
}
