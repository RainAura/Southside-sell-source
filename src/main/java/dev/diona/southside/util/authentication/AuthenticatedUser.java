package dev.diona.southside.util.authentication;

public final class AuthenticatedUser {
    private final String name;

    public AuthenticatedUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
