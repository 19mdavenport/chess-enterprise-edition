package chess;

import chess.factories.piecemoves.PieceMovesStrategyFactory;
import chess.factories.piecemoves.PieceMovesStrategyFactoryFactory;
import chess.strategies.piecemoves.PieceMovesStrategy;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;

    private final PieceType pieceType;


    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
    }


    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
    }


    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }


    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesStrategyFactoryFactory strategyFactoryFactory = new PieceMovesStrategyFactoryFactory(pieceType);
        PieceMovesStrategyFactory<?> strategyFactory = strategyFactoryFactory.get();
        PieceMovesStrategy pieceMovesStrategy = strategyFactory.get();
        return pieceMovesStrategy.pieceMoves(board, myPosition);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece o1 = (ChessPiece) o;
        return teamColor == o1.teamColor && pieceType == o1.pieceType;
    }


    @Override
    public int hashCode() {
        int result = teamColor != null ? teamColor.hashCode() : 0;
        result = 31 * result + (pieceType != null ? pieceType.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        char c = switch (pieceType) {
            case KING -> 'k';
            case QUEEN -> 'q';
            case BISHOP -> 'b';
            case KNIGHT -> 'n';
            case ROOK -> 'r';
            case PAWN -> 'p';
        };
        return switch (teamColor) {
            case WHITE -> String.valueOf(c).toUpperCase();
            case BLACK -> String.valueOf(c).toLowerCase();
        };
    }

}
