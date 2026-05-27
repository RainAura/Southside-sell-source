package cc.polyfrost.oneconfig.config.options.impl;

import cc.polyfrost.oneconfig.config.data.OptionType;
import cc.polyfrost.oneconfig.config.options.Option;
import org.jetbrains.annotations.NotNull;

public class DualOption extends Option<Boolean> {


    private final String left;

    private final String right;

    private final String description;

    private final int size;

    public DualOption(@NotNull String label, String left, String right, String description, int size, @NotNull Boolean defaultValue) {
        super(label, defaultValue);
        this.left = left;
        this.right = right;
        this.description = description;
        this.size = size;
    }

    @Override
    public OptionType type() {
        return OptionType.DUAL_OPTION;
    }


    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    public String getDescription() {
        return description;
    }

    public int getSize() {
        return size;
    }
}
