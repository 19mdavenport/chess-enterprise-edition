package chess;

/**
 * Enum identifying the 2 possible teams in a chess game
 */
public enum TeamColor {
    WHITE, BLACK;


    public TeamColor getOpposite() {
        return (this == WHITE) ? BLACK : WHITE;
    }
}
