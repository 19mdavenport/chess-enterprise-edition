package web;

import chess.ChessGame;

public interface WebSocketClientObserver {
    void loadGame(ChessGame game);
    void notify(String message);
    void error(String message);
}
