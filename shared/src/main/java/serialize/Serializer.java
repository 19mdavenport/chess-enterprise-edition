package serialize;

import chess.ruleset.extra.ExtraRuleset;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class Serializer {
    private static final String CLASS_NAME = "className";

    private static final Gson GSON;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ExtraRuleset.class, new TypeAdapter<ExtraRuleset>() {
            @Override
            public void write(JsonWriter jsonWriter, ExtraRuleset extraRuleset) throws IOException {
                JsonElement element = GSON.toJsonTree(extraRuleset);
                element.getAsJsonObject().addProperty(CLASS_NAME, extraRuleset.getClass().getName());
                GSON.toJson(element, jsonWriter);
            }

            @Override
            public ExtraRuleset read(JsonReader jsonReader) throws IOException {
                try {
                    JsonElement element = JsonParser.parseReader(jsonReader);
                    Class<?> clazz = Class.forName(element.getAsJsonObject().get(CLASS_NAME).getAsString());
                    return GSON.fromJson(element, (Class<? extends ExtraRuleset>) clazz);
                } catch (ClassNotFoundException e) {
                    throw new IOException(e);
                }
            }
        });
        GSON = gsonBuilder.create();
    }

    public static String serialize(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }


}
