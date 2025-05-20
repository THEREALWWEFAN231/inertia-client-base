package com.inertiaclient.base.utils;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    public static void writeToFile(File file, byte[] toWrite) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(toWrite);
        fileOutputStream.close();
    }

    public static void writeToFile(File file, String toWrite) throws Exception {
        FileUtils.writeToFile(file, toWrite.getBytes(StandardCharsets.UTF_8));//UTF-8 allows unicode characters
    }

    public static String getTextFromInputStream(InputStream inputStream) throws Exception {
        //this is "super fast", much faster then BufferedReader
        return FileUtils.getByteStreamFromInputStream(inputStream).toString(StandardCharsets.UTF_8);//UTF-8 allows unicode characters
    }

    public static ByteArrayOutputStream getByteStreamFromInputStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }

        byteArrayOutputStream.close();//says it does nothing, but who cares
        inputStream.close();
        return byteArrayOutputStream;
    }

    public static String getTextFromFile(File file) throws Exception {
        return FileUtils.getTextFromInputStream(new FileInputStream(file));
    }

    public static JsonElement getJsonElementFromFile(File file) throws Exception {
        return JsonParser.parseString(FileUtils.getTextFromFile(file));
    }

    public static JsonObject getJsonObjectFromFile(File file) throws Exception {
        return FileUtils.getJsonElementFromFile(file).getAsJsonObject();
    }

    public static JsonArray getJsonArrayFromFile(File file) throws Exception {
        return FileUtils.getJsonElementFromFile(file).getAsJsonArray();
    }

    public static JsonObject getJsonObject(JsonObject parent, String name) {
        JsonElement jsonElement = parent.get(name);
        if (jsonElement != null && jsonElement.isJsonObject()) {//basically the same as has(name), but faster
            return jsonElement.getAsJsonObject();
        }
        return null;
    }

    public static JsonArray getJsonArray(JsonObject parent, String name) {
        JsonElement jsonElement = parent.get(name);
        if (jsonElement != null && jsonElement.isJsonArray()) {//basically the same as has(name), but faster
            return jsonElement.getAsJsonArray();
        }
        return null;
    }

}
