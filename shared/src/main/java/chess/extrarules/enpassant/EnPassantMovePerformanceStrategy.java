package chess.extrarules.enpassant;

import chess.*;
import chess.strategies.performmove.MovePerformanceStrategy;

import java.util.Objects;

public class EnPassantMovePerformanceStrategy implements MovePerformanceStrategy {
    private final EnPassantRules rules;

    public EnPassantMovePerformanceStrategy(EnPassantRules rules) {
        this.rules = rules;
    }

    @Override
    public void performMove(ChessMove move, ChessBoard board) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (rules.getEnPassantPosition() == null || piece.getPieceType() != PieceType.PAWN ||
                Objects.equals(move.getStartPosition().getColumn(), move.getEndPosition().getColumn()) ||
                board.getPiece(move.getEndPosition()) != null ||
                !Objects.equals(move.getStartPosition().getRow(), rules.getEnPassantPosition().getRow()) ||
                !Objects.equals(move.getEndPosition().getColumn(), rules.getEnPassantPosition().getColumn())) {
            throw new InvalidMoveException("Invalid en passant move");
        }
        board.addPiece(rules.getEnPassantPosition(), null);
        ChessPiece pawn = board.getPiece(move.getStartPosition());
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), pawn);
    }


}
