package serialize;

import chess.ruleset.extra.ExtraRuleset;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.lang.reflect.Type;

public class Serializer {

    static final Gson GSON;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ExtraRuleset.class, new ExtraRulesetAdapter());
        gsonBuilder.registerTypeAdapter(UserGameCommand.class, new UserGameCommandDeserializer());
        gsonBuilder.registerTypeAdapter(ServerMessage.class, new ServerMessageDeserializer());
        GSON = gsonBuilder.create();
    }

    public static String serialize(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

}
