package chess.ruleset.piece;


import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class KnightRuleset implements PieceRuleset {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<>();

        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                if (myPosition.getRow() + i * 2 >= 1 && myPosition.getRow() + i * 2 <= 8 &&
                        myPosition.getColumn() + j >= 1 && myPosition.getColumn() + j <= 8) {
                    moves.add(new ChessMove(myPosition,
                            new ChessPosition(myPosition.getRow() + i * 2, myPosition.getColumn() + j)));
                }
                if (myPosition.getRow() + i >= 1 && myPosition.getRow() + i <= 8 &&
                        myPosition.getColumn() + j * 2 >= 1 && myPosition.getColumn() + j * 2 <= 8) {
                    moves.add(new ChessMove(myPosition,
                            new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + j * 2)));
                }
            }
        }

        Iterator<ChessMove> iterator = moves.iterator();
        while (iterator.hasNext()) {
            ChessMove move = iterator.next();
            ChessPiece movePiece = board.getPiece(move.getEndPosition());
            if (movePiece != null && movePiece.getTeamColor() == board.getPiece(myPosition).getTeamColor())
                iterator.remove();
        }

        return moves;
    }

}
