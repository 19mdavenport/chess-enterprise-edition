package chess.strategies.piecemoves;


import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;


public class QueenPieceMovesStrategy implements PieceMovesStrategy {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();
        moves.addAll(new BishopPieceMovesStrategy().pieceMoves(board, myPosition));
        moves.addAll(new RookPieceMovesStrategy().pieceMoves(board, myPosition));
        return moves;
    }

}
