package serialize;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.lang.reflect.Type;

public class ServerMessageDeserializer implements JsonDeserializer<ServerMessage> {

    @Override
    public ServerMessage deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        String messageValue = el.getAsJsonObject().get("serverMessageType").getAsString();
        ServerMessage.ServerMessageType message = ServerMessage.ServerMessageType.valueOf(messageValue);
        if(message == null) {
            throw new JsonParseException("Invalid serverMessageType");
        }
        Class<? extends ServerMessage> target = switch (message) {
            case LOAD_GAME -> LoadGameMessage.class;
            case ERROR -> ErrorMessage.class;
            case NOTIFICATION -> NotificationMessage.class;
        };
        return ctx.deserialize(el, target);
    }
}
