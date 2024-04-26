package chess.ruleset.piece;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public abstract class LineMoveRuleset implements PieceRuleset {

    protected Collection<ChessMove> lineMove(ChessBoard board, ChessPosition myPosition, int vertical, int horizontal) {
        Collection<ChessMove> moves = new HashSet<>();

        ChessPosition newPos;

        try {
            newPos = new ChessPosition(myPosition.getRow() + vertical, myPosition.getColumn() + horizontal);
        } catch (IllegalArgumentException e) {
            return moves;
        }

        while (true) {
            ChessPiece newPosPiece = board.getPiece(newPos);
            if (newPosPiece == null) {
                moves.add(new ChessMove(myPosition, newPos));
            } else {
                if (newPosPiece.getTeamColor() != board.getPiece(myPosition).getTeamColor())
                    moves.add(new ChessMove(myPosition, newPos));
                break;
            }
            try {
                newPos = new ChessPosition(newPos.getRow() + vertical, newPos.getColumn() + horizontal);
            } catch (IllegalArgumentException e) {
                break;
            }
        }
        return moves;
    }

}
