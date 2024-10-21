package chess.extrarules.enpassant;

import chess.*;
import chess.observers.BoardSetObserver;
import chess.observers.MoveMadeObserver;
import chess.extrarules.ExtraRuleset;
import chess.strategies.movematch.MoveMatcherStrategy;
import chess.strategies.performmove.MovePerformanceStrategy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class EnPassantRules implements ExtraRuleset {

    private ChessPosition enPassantPosition = null;

    ChessPosition getEnPassantPosition() {
        return enPassantPosition;
    }

    void setEnPassantPosition(ChessPosition enPassantPosition) {
        this.enPassantPosition = enPassantPosition;
    }

    @Override
    public MovePerformanceStrategy getMovePerformanceStrategy() {
        return new EnPassantMovePerformanceStrategy(this);
    }

    @Override
    public BoardSetObserver getBoardSetObserver() {
        return new EnPassantBoardSetObserver(this);
    }

    @Override
    public MoveMadeObserver getMoveMadeObserver() {
        return new EnPassantMoveMadeObserver(this);
    }

    @Override
    public MoveMatcherStrategy getMoveMatcherStrategy() {
        return new EnPassantMoveMatcherStrategy();
    }


    @Override
    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> ret = new HashSet<>();

        if (enPassantPosition == null) {
            return ret;
        }

        ChessPiece piece = board.getPiece(position);

        if (piece == null || piece.getPieceType() != PieceType.PAWN) {
            return ret;
        }

        if (enPassantPosition.getRow() == position.getRow() &&
                Math.abs(enPassantPosition.getColumn() - position.getColumn()) == 1) {
            int row = position.getRow() + ((piece.getTeamColor() == TeamColor.WHITE) ? 1 : -1);
            ret.add(new ChessMove(position, new ChessPosition(row, enPassantPosition.getColumn())));
        }

        return ret;
    }

    @Override
    public int hashCode() {
        return enPassantPosition != null ? enPassantPosition.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EnPassantRules obj1 = (EnPassantRules) obj;
        return Objects.equals(enPassantPosition, obj1.enPassantPosition);
    }

    @Override
    public String toString() {
        return enPassantPosition != null ? enPassantPosition.toString() : "";
    }

}
