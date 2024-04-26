package chess.ruleset.piece;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class KingRuleset implements PieceRuleset {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = myPosition.getRow() + i;
                int newCol = myPosition.getColumn() + j;
                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition pos = new ChessPosition(newRow, newCol);
                    ChessPiece posPiece = board.getPiece(pos);
                    if (posPiece == null || posPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        moves.add(new ChessMove(myPosition, pos));
                    }
                }
            }
        }

        return moves;
    }

}