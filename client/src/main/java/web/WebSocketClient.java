package web;

import chess.ChessMove;
import data.DataCache;
import serialize.Serializer;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClient extends Endpoint implements MessageHandler.Whole<String> {

    private final WebSocketClientObserver observer;

    private final Session session;

    public WebSocketClient(WebSocketClientObserver observer, String host, int port)
            throws URISyntaxException, DeploymentException, IOException {
        this.observer = observer;
        URI uri = new URI(String.format("ws://%s:%d/ws", host, port));
        session = ContainerProvider.getWebSocketContainer().connectToServer(this, uri);
        session.addMessageHandler(this);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    @Override
    public void onMessage(String s) {
        try {
            ServerMessage message = Serializer.deserialize(s, ServerMessage.class);
            switch (message.getServerMessageType()) {
                case LOAD_GAME -> observer.loadGame(Serializer.deserialize(s, LoadGameMessage.class).getGame());
                case NOTIFICATION -> observer.notify(Serializer.deserialize(s, NotificationMessage.class).getMessage());
                case ERROR -> observer.error(Serializer.deserialize(s, ErrorMessage.class).getErrorMessage());
            }
        } catch (Exception e) {
            observer.error(e.getMessage());
        }
    }

    public void connect() throws IOException {
        sendMessage(new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                DataCache.getInstance().getAuthToken(),
                DataCache.getInstance().getGameId()));
    }

    public void makeMove(ChessMove move) throws IOException {
        sendMessage(new MakeMoveCommand(DataCache.getInstance().getAuthToken(), DataCache.getInstance().getGameId(), move));
    }

    public void leave() throws IOException {
        sendMessage(new UserGameCommand(UserGameCommand.CommandType.LEAVE,
                DataCache.getInstance().getAuthToken(),
                DataCache.getInstance().getGameId()));
    }

    public void resign() throws IOException {
        sendMessage(new UserGameCommand(UserGameCommand.CommandType.RESIGN,
                DataCache.getInstance().getAuthToken(),
                DataCache.getInstance().getGameId()));
    }

    private void sendMessage(UserGameCommand command) throws IOException {
        session.getBasicRemote().sendText(Serializer.serialize(command));
    }
}
