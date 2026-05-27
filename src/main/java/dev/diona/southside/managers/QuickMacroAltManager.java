//package dev.diona.southside.managers;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import dev.diona.southside.Southside;
//import dev.diona.southside.module.Serializable;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class QuickMacroAltManager implements Serializable {
//    private static final File ALT_FILE = new File(FileManager.CLIENT_DIRECTORY, "cookie.json");
//    public final List<String> cookies = new ArrayList<>();
//
//    public QuickMacroAltManager() {
//        this.deserialize(Southside.fileManager.readFileData(ALT_FILE).getAsJsonObject().get("cookie"));
//    }
//
//    @Override
//    public JsonElement serialize() {
//        JsonArray array = new JsonArray();
//        for (int i = 0; i < cookies.size(); i++) {
//            array.add(i);
//        }
//        return null;
//    }
//
//    public void save() {
//        Southside.fileManager.writeData(ALT_FILE, this.serialize());
//    }
//
//    @Override
//    public void deserialize(JsonElement element) {
//        cookies.clear();
//        try {
//            var arr = element.getAsJsonArray();
//            for (int i = 0; i < arr.size(); i++) {
//                cookies.add(arr.get(i).getAsString());
//            }
//        } catch (NullPointerException | UnsupportedOperationException | IllegalStateException e) {
//            cookies.clear();
//        }
//    }
//}
