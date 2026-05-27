package cc.polyfrost.oneconfig.config.options.impl;


import cc.polyfrost.oneconfig.config.data.OptionType;
import cc.polyfrost.oneconfig.config.options.Option;
import dev.diona.southside.util.misc.MathUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Dropdown extends Option<Integer> {
    private final String[] options;

    private final String description;

    private final int size;

    public Dropdown(@NotNull String label, String[] options, String description, int size, @NotNull Integer defaultValue) {
        super(label, defaultValue);
        this.options = options;
        this.description = description;
        this.size = size;
    }

    public Dropdown(String name, String value, String... values) {
        super(name, Arrays.asList(values).indexOf(value));
        this.options = values;
        this.description = "";
        this.size = 1;

    }

    @Override
    public void setValue(Integer value) {
        if (options != null) {
            super.setValue(MathUtil.clamp(value, 0, options.length - 1));
        }
    }

    public String getMode() {
        return options[this.getValue()];
    }

    @Override
    public OptionType type() {
        return OptionType.DROPDOWN;
    }

    public String[] getOptions() {
        return options;
    }

    public String getDescription() {
        return description;
    }

    public int getSize() {
        return size;
    }

    public boolean isMode(String classic) {
        return options[this.getValue()].equals(classic);
    }
}
