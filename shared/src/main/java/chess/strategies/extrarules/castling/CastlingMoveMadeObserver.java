package chess.strategies.extrarules.castling;

import chess.*;
import chess.observers.MoveMadeObserver;

public class CastlingMoveMadeObserver implements MoveMadeObserver {
    private final CastlingRules rules;

    public CastlingMoveMadeObserver(CastlingRules rules) {this.rules = rules;}

    @Override
    public void moveMade(ChessMove move, ChessBoard board) {
        boolean[] castlingOptions = rules.getCastlingOptions();
        ChessPiece piece = board.getPiece(move.getEndPosition());
        if (piece.getPieceType() == PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                castlingOptions[0] = false;
                castlingOptions[1] = false;
            } else {
                castlingOptions[2] = false;
                castlingOptions[3] = false;
            }
        } else if (piece.getPieceType() == PieceType.ROOK) {
            ChessPosition startPos = move.getStartPosition();
            if (castlingOptions[0] && startPos.getRow() == 1 && startPos.getColumn() == 8) {
                castlingOptions[0] = false;
            }
            if (castlingOptions[1] && startPos.getRow() == 1 && startPos.getColumn() == 1) {
                castlingOptions[1] = false;
            }
            if (castlingOptions[2] && startPos.getRow() == 8 && startPos.getColumn() == 8) {
                castlingOptions[2] = false;
            }
            if (castlingOptions[3] && startPos.getRow() == 8 && startPos.getColumn() == 1) {
                castlingOptions[3] = false;
            }
        }
        rules.setCastlingOptions(castlingOptions);
    }
}
