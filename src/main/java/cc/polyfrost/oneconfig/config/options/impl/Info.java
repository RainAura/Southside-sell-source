
package cc.polyfrost.oneconfig.config.options.impl;


import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.OptionType;
import cc.polyfrost.oneconfig.config.options.Option;
import org.jetbrains.annotations.NotNull;

public class Info extends Option<String> {

    private final InfoType type;

    private final int size;

    public Info(@NotNull String label, InfoType type, int size, @NotNull String defaultValue) {
        super(label, defaultValue);
        this.type = type;
        this.size = size;
    }


    @Override
    public OptionType type() {
        return OptionType.INFO;
    }

    public InfoType getType() {
        return type;
    }

    public int getSize() {
        return size;
    }
}
