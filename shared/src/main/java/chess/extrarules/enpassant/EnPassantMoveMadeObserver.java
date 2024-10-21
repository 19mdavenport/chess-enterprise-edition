package chess.extrarules.enpassant;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.PieceType;
import chess.observers.MoveMadeObserver;

public class EnPassantMoveMadeObserver implements MoveMadeObserver {

    private final EnPassantRules rules;

    public EnPassantMoveMadeObserver(EnPassantRules rules) {
        this.rules = rules;
    }

    @Override
    public void moveMade(ChessMove move, ChessBoard board) {
        ChessPiece piece = board.getPiece(move.getEndPosition());
        if (piece.getPieceType() == PieceType.PAWN &&
                Math.abs(move.getStartPosition().getRow() - move.getEndPosition().getRow()) == 2) {
            rules.setEnPassantPosition(move.getEndPosition());
        } else {
            rules.setEnPassantPosition(null);
        }
    }
}
