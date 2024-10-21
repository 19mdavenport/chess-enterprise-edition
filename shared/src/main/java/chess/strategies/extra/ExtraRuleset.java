package chess.strategies.extra;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.strategies.performmove.MovePerformanceStrategy;

import java.util.Collection;

public interface ExtraRuleset {
    MovePerformanceStrategy getMovePerformanceStrategy();

    boolean isMoveMatch(ChessMove move, ChessBoard board);

    void moveMade(ChessMove move, ChessBoard board);

    void setBoard(ChessBoard board);

    Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position);
}
