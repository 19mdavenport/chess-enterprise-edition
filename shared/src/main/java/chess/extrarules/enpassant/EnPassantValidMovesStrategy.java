package chess.extrarules.enpassant;

import chess.*;
import chess.strategies.validmoves.ValidMovesStrategy;

import java.util.Collection;
import java.util.HashSet;

public class EnPassantValidMovesStrategy implements ValidMovesStrategy {
    private final EnPassantRules rules;

    public EnPassantValidMovesStrategy(EnPassantRules rules) {this.rules = rules;}

    @Override
    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> ret = new HashSet<>();

        ChessPosition enPassantPosition = rules.getEnPassantPosition();
        if (enPassantPosition == null) {
            return ret;
        }

        ChessPiece piece = board.getPiece(position);

        if (piece == null || piece.getPieceType() != PieceType.PAWN) {
            return ret;
        }

        if (enPassantPosition.getRow() == position.getRow() &&
                Math.abs(enPassantPosition.getColumn() - position.getColumn()) == 1) {
            int row = position.getRow() + ((piece.getTeamColor() == TeamColor.WHITE) ? 1 : -1);
            ret.add(new ChessMove(position, new ChessPosition(row, enPassantPosition.getColumn())));
        }

        return ret;
    }

}
