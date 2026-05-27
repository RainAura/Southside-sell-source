package dev.diona.southside.module;

import com.google.gson.JsonElement;

public interface Serializable {
    JsonElement serialize();

    void deserialize(JsonElement element);
}
