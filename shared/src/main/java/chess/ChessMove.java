package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition endPosition;

    private final PieceType promotionPiece;

    private final ChessPosition startPosition;


    /**
     * Constructs a new move with provided positions
     *
     * @param startPosition Starting position of the move
     * @param endPosition   Ending position of the move
     */
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this(startPosition, endPosition, null);
    }


    /**
     * Constructs a new move with provided positions and promotion piece
     *
     * @param startPosition  Starting position of the move
     * @param endPosition    Ending position of the move
     * @param promotionPiece Piece to promote to during the move
     */
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, PieceType promotionPiece) {
        if (promotionPiece == PieceType.KING || promotionPiece == PieceType.PAWN) {
            throw new IllegalArgumentException("Invalid promotion piece");
        }
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public PieceType getPromotionPiece() {
        return promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    @Override
    public int hashCode() {
        int result = startPosition != null ? startPosition.hashCode() : 0;
        result = 31 * result + (endPosition != null ? endPosition.hashCode() : 0);
        result = 31 * result + (promotionPiece != null ? promotionPiece.hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) obj;
        return Objects.equals(startPosition, chessMove.startPosition) &&
                Objects.equals(endPosition, chessMove.endPosition) && promotionPiece == chessMove.promotionPiece;
    }


    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();

        ret.append(startPosition.toString());
        ret.append(endPosition.toString());

        if (promotionPiece != null) {
            ret.append(switch (promotionPiece) {
                case QUEEN -> 'q';
                case BISHOP -> 'b';
                case KNIGHT -> 'n';
                case ROOK -> 'r';
                case KING, PAWN -> throw new IllegalStateException("Invalid promotion piece: " + promotionPiece);
            });
        }

        return ret.toString();
    }

}
