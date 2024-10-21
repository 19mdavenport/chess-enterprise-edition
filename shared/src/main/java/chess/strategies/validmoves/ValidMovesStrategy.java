package chess.strategies.validmoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface ValidMovesStrategy {
    Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position);
}
