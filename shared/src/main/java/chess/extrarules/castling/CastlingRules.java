package chess.extrarules.castling;

import chess.extrarules.ExtraRuleset;
import chess.observers.BoardSetObserver;
import chess.observers.MoveMadeObserver;
import chess.strategies.validmoves.ValidMovesStrategy;
import chess.strategies.movematch.MoveMatcherStrategy;
import chess.strategies.performmove.MovePerformanceStrategy;

import java.util.Arrays;

public class CastlingRules implements ExtraRuleset {

    private boolean[] castlingOptions;

    public CastlingRules() {
        castlingOptions = new boolean[4];
        Arrays.fill(castlingOptions, true);
    }

    boolean[] getCastlingOptions() {
        return castlingOptions.clone();
    }

    void setCastlingOptions(boolean[] castlingOptions) {
        this.castlingOptions = castlingOptions.clone();
    }

    @Override
    public MovePerformanceStrategy getMovePerformanceStrategy() {
        return new CastlingMovePerformanceStrategy();
    }

    @Override
    public BoardSetObserver getBoardSetObserver() {
        return new CastlingBoardSetObserver(this);
    }

    @Override
    public MoveMadeObserver getMoveMadeObserver() {
        return new CastlingMoveMadeObserver(this);
    }

    @Override
    public MoveMatcherStrategy getMoveMatcherStrategy() {
        return new CastlingMoveMatcherStrategy();
    }

    @Override
    public ValidMovesStrategy getValidMovesStrategy() {
        return new CastlingValidMovesStrategy(this);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(castlingOptions);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CastlingRules obj1 = (CastlingRules) obj;
        return Arrays.equals(castlingOptions, obj1.castlingOptions);
    }


    @Override
    public String toString() {
        return "%s%s%s%s".formatted(castlingOptions[0] ? 'K' : '-', castlingOptions[1] ? 'Q' : '-',
                castlingOptions[2] ? 'k' : '-', castlingOptions[3] ? 'q' : '-');
    }

}
