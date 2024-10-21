package chess.extrarules.castling;

import chess.*;
import chess.strategies.validmoves.ValidMovesStrategy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class CastlingValidMovesStrategy implements ValidMovesStrategy {
    private final CastlingRules rules;

    public CastlingValidMovesStrategy(CastlingRules rules) {this.rules = rules;}

    @Override
    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position) {
        boolean[] castlingOptions = rules.getCastlingOptions();
        Collection<ChessMove> ret = new HashSet<>();

        ChessPiece piece = board.getPiece(position);

        if (piece == null || piece.getPieceType() != PieceType.KING || position.getColumn() != 5 ||
                ((piece.getTeamColor() == TeamColor.WHITE && position.getRow() != 1) ||
                        (piece.getTeamColor() == TeamColor.BLACK && position.getRow() != 8))) {
            return ret;
        }

        int offset = piece.getTeamColor() == TeamColor.WHITE ? 0 : 2;

        if (castlingOptions[offset]) {
            Optional<ChessMove> kingSideCastle = singleCastlingMove(board, piece.getTeamColor(), true);
            kingSideCastle.ifPresent(ret::add);
        }
        if (castlingOptions[offset + 1]) {
            Optional<ChessMove> queenSideCastle = singleCastlingMove(board, piece.getTeamColor(), false);
            queenSideCastle.ifPresent(ret::add);
        }

        return ret;
    }

    private Optional<ChessMove> singleCastlingMove(ChessBoard board, TeamColor color, boolean kingSide) {
        if (ChessGame.isInCheck(color, board)) {
            return Optional.empty();
        }

        int row = (color == TeamColor.WHITE) ? 1 : 8;
        int col = (kingSide) ? 6 : 4;

        for (int i = col; i < 8 && i > 1; i += col - 5) {
            if (board.getPiece(new ChessPosition(row, i)) != null) {
                return Optional.empty();
            }
        }

        ChessPosition orig = new ChessPosition(row, 5);
        ChessBoard betweenBoard = new ChessBoard(board);
        betweenBoard.addPiece(new ChessPosition(row, col), betweenBoard.getPiece(orig));
        betweenBoard.addPiece(orig, null);
        if (ChessGame.isInCheck(color, betweenBoard)) {
            return Optional.empty();
        }

        ChessPosition end = new ChessPosition(row, 5 + 2 * (col - 5));
        ChessMove out = new ChessMove(orig, end);
        ChessBoard outBoard = new ChessBoard(board);
        try {
            rules.getMovePerformanceStrategy().performMove(out, outBoard);
        } catch (InvalidMoveException e) {
            return Optional.empty();
        }
        return Optional.ofNullable((ChessGame.isInCheck(color, outBoard)) ? null : out);
    }
}
