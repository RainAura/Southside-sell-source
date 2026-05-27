package dev.diona.southside.util.quickmacro.metadatas;

public class PartyRequestMetadata {
    private final String name;
    private final String acceptId;
    private final String denyId;

    public PartyRequestMetadata(String name, String acceptId, String denyId) {
        this.name = name;
        this.acceptId = acceptId;
        this.denyId = denyId;
    }

    public String getName() {
        return name;
    }

    public String getAcceptId() {
        return acceptId;
    }

    public String getDenyId() {
        return denyId;
    }
}