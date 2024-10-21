package chess.strategies.performmove;

import chess.ChessBoard;
import chess.ChessMove;
import chess.InvalidMoveException;

public interface MovePerformanceStrategy {
    void performMove(ChessMove move, ChessBoard board) throws InvalidMoveException;
}
