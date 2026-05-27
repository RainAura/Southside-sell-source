package cc.polyfrost.oneconfig.config.options.impl;

import cc.polyfrost.oneconfig.config.data.OptionType;
import cc.polyfrost.oneconfig.config.options.Option;
import cc.polyfrost.oneconfig.hud.Hud;
import org.jetbrains.annotations.NotNull;

public class HUD extends Option<Hud> {

    public HUD(@NotNull String label, @NotNull Hud defaultValue) {
        super(label, defaultValue);
    }

    @Override
    public OptionType type() {
        return null;
    }
}
