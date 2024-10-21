package chess.strategies.performmove;

import chess.*;

public class PromotionMovePerformanceStrategy implements MovePerformanceStrategy {
    @Override
    public void performMove(ChessMove move, ChessBoard board) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece.getPieceType() != PieceType.PAWN) {
            throw new InvalidMoveException("Move with promotion piece not on pawn");
        }
        if ((piece.getTeamColor() == TeamColor.WHITE && move.getEndPosition().getRow() != 8) ||
                (piece.getTeamColor() == TeamColor.BLACK && move.getEndPosition().getRow() != 1)) {
            throw new InvalidMoveException("Move with promotion piece doesn't end on correct row");
        }

        TeamColor color = board.getPiece(move.getStartPosition()).getTeamColor();
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), new ChessPiece(color, move.getPromotionPiece()));
    }
}
