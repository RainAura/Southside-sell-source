package dev.diona.southside.util.misc;

import com.google.gson.JsonObject;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;

import static net.minecraft.util.text.ITextComponent.Serializer.GSON;

public class FileUtil {

    public static String readFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void writeFile(File file, String content) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(content);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static JsonObject readFileAsJson(File file) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            JsonObject jsonObject = GSON.fromJson(bufferedReader, JsonObject.class);
            bufferedReader.close();
            fileReader.close();
            if (jsonObject == null) {
                return new JsonObject();
            }
            return jsonObject;
        } catch (FileNotFoundException e) {
            return new JsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ByteBuffer resourceToByteBufferNullable(String path, Class<?> clazz) {
        try {
            return resourceToByteBuffer(path, clazz);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static ByteBuffer resourceToByteBuffer(String path, Class<?> clazz) throws IOException {
        byte[] bytes;
        path = path.trim();
        InputStream stream;
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            stream = Files.newInputStream(file.toPath());
        } else {
            stream = clazz.getResourceAsStream(path);
        }
        if (stream == null) {
            throw new FileNotFoundException(path);
        }
        bytes = org.apache.commons.io.IOUtils.toByteArray(stream);
        ByteBuffer data = ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.nativeOrder())
                .put(bytes);
        ((Buffer) data).flip();
        return data;
    }

}
