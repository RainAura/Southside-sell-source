package dev.diona.southside.module;

public class NonToggleableModule implements BaseModule {
    private final String name, description;

    public NonToggleableModule(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
