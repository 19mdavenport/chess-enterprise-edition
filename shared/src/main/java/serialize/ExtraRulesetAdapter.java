package serialize;

import chess.strategies.extra.ExtraRuleset;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ExtraRulesetAdapter extends TypeAdapter<ExtraRuleset> {
    private static final String CLASS_NAME = "className";

    @Override
    public void write(JsonWriter jsonWriter, ExtraRuleset extraRuleset) throws IOException {
        JsonElement element = Serializer.GSON.toJsonTree(extraRuleset);
        element.getAsJsonObject().addProperty(CLASS_NAME, extraRuleset.getClass().getName());
        Serializer.GSON.toJson(element, jsonWriter);
    }

    @Override
    public ExtraRuleset read(JsonReader jsonReader) throws IOException {
        try {
            JsonElement element = JsonParser.parseReader(jsonReader);
            Class<?> clazz = Class.forName(element.getAsJsonObject().get(CLASS_NAME).getAsString());
            return Serializer.GSON.fromJson(element, (Class<? extends ExtraRuleset>) clazz);
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}
