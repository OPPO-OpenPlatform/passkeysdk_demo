package com.osec.fido2test.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class JsonUtil {
    private static final Gson mGson;

    static {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.registerTypeHierarchyAdapter(byte[].class, new ByteArrayToHexTypeAdapter());
        mGson = builder.create();
    }

    public static String toJson(Object src) {
        return mGson.toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return mGson.fromJson(json, classOfT);
    }

    private static class ByteArrayToHexTypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ByteUtil.hex2byte(json.getAsString());
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(ByteUtil.byte2hex(src));
        }
    }
}
