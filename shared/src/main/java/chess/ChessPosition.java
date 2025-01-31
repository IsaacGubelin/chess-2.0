package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private static final int GRID_SIZE = 8;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Private data
     */
    private int row;
    private int col;


    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }

    /**
     * Check that the row and column are valid
     */
    public boolean isInBounds() {
        return (row <= GRID_SIZE && row > 0 && col <= GRID_SIZE && col > 0);
    }
    /**
     * Increment the row
     */
    public void incRow() {
        row += 1;
    }

    /**
     * Decrement the row
     */
    public void decRow() {
        row -= 1;
    }

    /**
     * Increment the column
     */
    public void incCol() {
        col += 1;
    }

    /**
     * Decrement the column
     */
    public void decCol() {
        col -= 1;
    }

    /**
     * Get a copy of this chess position object
     * @return clone of chess position
     */
    @Override
    public ChessPosition clone() {
        return new ChessPosition(row, col);
    }


    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
