package dev.diona.southside.util.authentication;

public enum AuthenticationStatus {
    INSTANCE;
    public String token;
    public AuthenticatedUser user;
    public String session;
    public int magic;
    public static final String BACKEND_ENDPOINT = "https://backend.mojang.xyz";

}
