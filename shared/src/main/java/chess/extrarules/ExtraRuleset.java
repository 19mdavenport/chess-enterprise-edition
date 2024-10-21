package chess.extrarules;

import chess.observers.BoardSetObserver;
import chess.observers.MoveMadeObserver;
import chess.strategies.validmoves.ValidMovesStrategy;
import chess.strategies.movematch.MoveMatcherStrategy;
import chess.strategies.performmove.MovePerformanceStrategy;

public interface ExtraRuleset {
    MovePerformanceStrategy getMovePerformanceStrategy();

    BoardSetObserver getBoardSetObserver();

    MoveMadeObserver getMoveMadeObserver();

    MoveMatcherStrategy getMoveMatcherStrategy();

    ValidMovesStrategy getValidMovesStrategy();
}
