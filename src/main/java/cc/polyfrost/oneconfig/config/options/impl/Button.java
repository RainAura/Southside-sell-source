package cc.polyfrost.oneconfig.config.options.impl;


import cc.polyfrost.oneconfig.config.data.OptionType;
import cc.polyfrost.oneconfig.config.options.Option;
import org.jetbrains.annotations.NotNull;

public class Button extends Option<Runnable> {

    private final String text;
    private final String description;

    private final int size;

    public Button(@NotNull String label, String text, String description, int size, @NotNull Runnable defaultValue) {
        super(label, defaultValue);
        this.text = text;
        this.description = description;
        this.size = size;
    }

    @Override
    public OptionType type() {
        return OptionType.BUTTON;
    }

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }

    public int getSize() {
        return size;
    }
}
