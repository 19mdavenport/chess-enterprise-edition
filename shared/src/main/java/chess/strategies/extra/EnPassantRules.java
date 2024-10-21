package chess.strategies.extra;

import chess.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class EnPassantRules implements ExtraRuleset {

    private ChessPosition enPassantPosition = null;


    @Override
    public void setBoard(ChessBoard board) {
        enPassantPosition = null;
    }


    public void moveMade(ChessMove move, ChessBoard board) {
        ChessPiece piece = board.getPiece(move.getEndPosition());
        if (piece.getPieceType() == PieceType.PAWN &&
                Math.abs(move.getStartPosition().getRow() - move.getEndPosition().getRow()) == 2) {
            enPassantPosition = move.getEndPosition();
        }
        else {
            enPassantPosition = null;
        }
    }


    @Override
    public boolean isMoveMatch(ChessMove move, ChessBoard board) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        return piece.getPieceType() == PieceType.PAWN &&
                !Objects.equals(move.getStartPosition().getColumn(), move.getEndPosition().getColumn()) &&
                board.getPiece(move.getEndPosition()) == null;
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


    public void performMove(ChessMove move, ChessBoard board) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (enPassantPosition == null || piece.getPieceType() != PieceType.PAWN ||
                Objects.equals(move.getStartPosition().getColumn(), move.getEndPosition().getColumn()) ||
                board.getPiece(move.getEndPosition()) != null ||
                !Objects.equals(move.getStartPosition().getRow(), enPassantPosition.getRow()) ||
                !Objects.equals(move.getEndPosition().getColumn(), enPassantPosition.getColumn())) {
            throw new InvalidMoveException("Invalid en passant move");
        }
        board.addPiece(enPassantPosition, null);
        ChessPiece pawn = board.getPiece(move.getStartPosition());
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), pawn);
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
    public int hashCode() {
        return enPassantPosition != null ? enPassantPosition.hashCode() : 0;
    }


    @Override
    public String toString() {
        return enPassantPosition != null ? enPassantPosition.toString() : "";
    }

}
