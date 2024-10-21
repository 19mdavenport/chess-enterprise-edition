package chess.strategies.extrarules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.observers.BoardSetObserver;
import chess.observers.MoveMadeObserver;
import chess.strategies.performmove.MovePerformanceStrategy;

import java.util.Collection;

public interface ExtraRuleset {
    MovePerformanceStrategy getMovePerformanceStrategy();

    BoardSetObserver getBoardSetObserver();

    MoveMadeObserver getMoveMadeObserver();

    boolean isMoveMatch(ChessMove move, ChessBoard board);

    Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position);
}
