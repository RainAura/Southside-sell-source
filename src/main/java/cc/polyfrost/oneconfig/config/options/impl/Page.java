package cc.polyfrost.oneconfig.config.options.impl;

import cc.polyfrost.oneconfig.config.data.OptionType;
import cc.polyfrost.oneconfig.config.data.PageLocation;
import cc.polyfrost.oneconfig.config.options.Option;
import org.jetbrains.annotations.NotNull;

public class Page extends Option<Object> {

    /**
     * If the page button is at the top or bottem of the page
     */
    private final PageLocation location;

    /**
     * The description of the page that will be displayed to the user
     */
    private final String description;

    public Page(@NotNull String label, PageLocation location, String description, @NotNull Object defaultValue) {
        super(label, defaultValue);
        this.location = location;
        this.description = description;
    }

    @Override
    public OptionType type() {
        return null;
    }

    public PageLocation getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }
}
