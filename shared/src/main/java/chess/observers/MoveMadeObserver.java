package chess.observers;

import chess.ChessBoard;
import chess.ChessMove;

public interface MoveMadeObserver {
    void moveMade(ChessMove move, ChessBoard board);
}
