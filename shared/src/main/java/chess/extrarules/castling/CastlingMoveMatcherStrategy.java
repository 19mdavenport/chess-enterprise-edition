package chess.extrarules.castling;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.PieceType;
import chess.strategies.movematch.MoveMatcherStrategy;

public class CastlingMoveMatcherStrategy implements MoveMatcherStrategy {
    @Override
    public boolean isMoveMatch(ChessMove move, ChessBoard board) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        return (piece.getPieceType() == PieceType.KING &&
                Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 2);
    }
}
