package chess.strategies.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface PieceMovesStrategy {

    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

}
