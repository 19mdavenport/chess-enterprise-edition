package chess.strategies.performmove;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;

public class NormalMovePerformanceStrategy implements MovePerformanceStrategy {
    @Override
    public void performMove(ChessMove move, ChessBoard board) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), piece);
    }
}
