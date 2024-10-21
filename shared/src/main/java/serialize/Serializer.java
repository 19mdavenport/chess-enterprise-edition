package serialize;

import chess.strategies.extrarules.ExtraRuleset;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

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
