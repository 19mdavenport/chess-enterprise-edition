package chess.extrarules.enpassant;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.PieceType;
import chess.strategies.movematch.MoveMatcherStrategy;

import java.util.Objects;

public class EnPassantMoveMatcherStrategy implements MoveMatcherStrategy {
    @Override
    public boolean isMoveMatch(ChessMove move, ChessBoard board) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        return piece.getPieceType() == PieceType.PAWN &&
                !Objects.equals(move.getStartPosition().getColumn(), move.getEndPosition().getColumn()) &&
                board.getPiece(move.getEndPosition()) == null;
    }
}
