package dev.diona.southside.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.diona.southside.Southside;
import dev.diona.southside.util.misc.FileUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static dev.diona.southside.Southside.MC.mc;
import static net.minecraft.util.text.ITextComponent.Serializer.GSON;

public class FileManager {
    public static final File CLIENT_DIRECTORY = new File(mc.gameDir, Southside.CLIENT_NAME);
    public static final File CLIENT_INFO = new File(CLIENT_DIRECTORY, Southside.CLIENT_NAME + ".json");

    public FileManager() {
        if (!CLIENT_DIRECTORY.exists()) {
            if (!CLIENT_DIRECTORY.mkdir()) {
                Southside.LOGGER.error("Failed to create the client file folder!");
            }
        }
        if (!CLIENT_INFO.exists()) {
            JsonObject defaultInfo = new JsonObject();
            defaultInfo.addProperty("config", "default");
            writeData(CLIENT_INFO, defaultInfo);
        }
    }

    public JsonElement readFileData(File file) {
        JsonObject jsonObject = FileUtil.readFileAsJson(file);
        if (jsonObject.get("data") == null) {
            writeData(file, new JsonObject());
            return new JsonObject();
        }
        return jsonObject.get("data");
    }

    public void createClientFile(File file) {
        try {
            if (!file.createNewFile()) {
                throw new RuntimeException("Failed to create client file.");
            }
            final JsonObject jsonObject = new JsonObject();

            this.updateMetadata(jsonObject);

            final FileWriter fileWriter = new FileWriter(file);
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            GSON.toJson(jsonObject, bufferedWriter);

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMetadata(JsonObject jsonObject) {
        String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        JsonObject metadataJsonObject = new JsonObject();
        try {
            metadataJsonObject = jsonObject.get("metadata").getAsJsonObject();
            assert jsonObject.get("version") != null;
            assert jsonObject.get("create") != null;
        } catch (NullPointerException | UnsupportedOperationException | AssertionError e) {
            metadataJsonObject.addProperty("version", Southside.CLIENT_VERSION);
            metadataJsonObject.addProperty("create", date);
        } finally {
            metadataJsonObject.addProperty("modify", date);
            jsonObject.add("metadata", metadataJsonObject);
        }
    }

    public void writeData(File file, JsonElement data) {
        try {
            if (!file.exists()) createClientFile(file);
            JsonObject jsonObject = FileUtil.readFileAsJson(file);
            jsonObject.add("data", data);

            updateMetadata(jsonObject);

            final FileWriter fileWriter = new FileWriter(file);
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject, bufferedWriter);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
