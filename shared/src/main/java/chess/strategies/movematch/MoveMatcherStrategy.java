package chess.strategies.movematch;

import chess.ChessBoard;
import chess.ChessMove;

public interface MoveMatcherStrategy {
    boolean isMoveMatch(ChessMove move, ChessBoard board);
}
