package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;

    private final int col;


    /**
     * Constructs a new Chess position for provided location
     *
     * @param row Row of the board this position is at
     * @param col Col of the board this position is at
     */
    public ChessPosition(int row, int col) {
        if (row < 1 || col < 1 || row > 8 || col > 8) {
            throw new IllegalArgumentException(row + ", " + col + " is not on the board");
        }
        this.row = row;
        this.col = col;
    }


    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }


    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }


    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ChessPosition other = (ChessPosition) obj;

        if (row != other.row) return false;
        return col == other.col;
    }


    @Override
    public String toString() {
        return "" + (char) (col + 96) + row;
    }

}
