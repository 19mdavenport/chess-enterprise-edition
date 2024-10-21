package chess.strategies.performmove.extra;

import chess.*;
import chess.strategies.performmove.MovePerformanceStrategy;

import java.util.Objects;

public record EnPassantMovePerformanceStrategy(ChessPosition enPassantPosition) implements MovePerformanceStrategy {

    @Override
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
}
