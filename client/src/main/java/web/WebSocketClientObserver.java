package web;

import chess.ChessGame;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public interface WebSocketClientObserver {
    void loadGame(ChessGame game);
    void notify(String message);
    void error(String message);
}
