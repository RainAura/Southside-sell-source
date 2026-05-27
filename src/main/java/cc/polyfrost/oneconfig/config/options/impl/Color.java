package cc.polyfrost.oneconfig.config.options.impl;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.OptionType;
import cc.polyfrost.oneconfig.config.options.Option;
import org.jetbrains.annotations.NotNull;

public class Color extends Option<OneColor> {
    private final String description;

    private final boolean allowAlpha;

    private final int size;

    public Color(@NotNull String label, String description, boolean allowAlpha, int size, @NotNull OneColor defaultValue) {
        super(label, defaultValue);
        this.description = description;
        this.allowAlpha = allowAlpha;
        this.size = size;
    }

    public Color(@NotNull String label, boolean allowAlpha, @NotNull OneColor defaultValue) {
        this(label, "", allowAlpha, 1, defaultValue);
    }

    public Color(@NotNull String label, String description, boolean allowAlpha, @NotNull OneColor defaultValue) {
        this(label, description, allowAlpha, 1, defaultValue);
    }

    public Color(@NotNull String label, String description, @NotNull OneColor defaultValue) {
        this(label, description, true, 1, defaultValue);
    }

    public Color(@NotNull String label, @NotNull OneColor defaultValue) {
        this(label, "", true, 1, defaultValue);
    }

    @Override
    public OptionType type() {
        return OptionType.COLOR;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAllowAlpha() {
        return allowAlpha;
    }

    public int getSize() {
        return size;
    }
}
