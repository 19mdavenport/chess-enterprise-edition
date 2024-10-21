package chess.strategies.extrarules.enpassant;

import chess.ChessBoard;
import chess.observers.BoardSetObserver;

public class EnPassantBoardSetObserver implements BoardSetObserver {

    private final EnPassantRules rules;

    public EnPassantBoardSetObserver(EnPassantRules rules) {
        this.rules = rules;
    }

    @Override
    public void setBoard(ChessBoard board) {
        rules.setEnPassantPosition(null);
    }
}
