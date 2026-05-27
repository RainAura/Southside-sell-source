package cc.polyfrost.oneconfig.config.options;

import cc.polyfrost.oneconfig.config.data.OptionType;

public interface IOption {

    OptionType type();

    default String category() {
        return "General";
    }

    default String subcategory() {
        return "";
    }


}
