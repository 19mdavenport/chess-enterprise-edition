package chess.strategies.extra.castling;

import chess.*;
import chess.strategies.performmove.MovePerformanceStrategy;

public class CastlingMovePerformanceStrategy implements MovePerformanceStrategy {
    @Override
    public void performMove(ChessMove move, ChessBoard board) throws InvalidMoveException {
        if (move.getPromotionPiece() != null ||
                board.getPiece(move.getStartPosition()).getPieceType() != PieceType.KING ||
                Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) != 2) {
            throw new InvalidMoveException("Not a valid castling move");
        }
        int oldColumn = (move.getStartPosition().getColumn() > move.getEndPosition().getColumn()) ? 1 : 8;
        ChessPosition oldPosition = new ChessPosition(move.getEndPosition().getRow(), oldColumn);

        ChessPosition newPosition = new ChessPosition(move.getEndPosition().getRow(),
                (move.getStartPosition().getColumn() + move.getEndPosition().getColumn()) / 2);

        board.addPiece(newPosition, board.getPiece(oldPosition));
        board.addPiece(oldPosition, null);

        ChessPiece king = board.getPiece(move.getStartPosition());
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), king);
    }
}
