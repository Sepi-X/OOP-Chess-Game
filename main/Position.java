package main;

/**
 * Represents a position on the chess board.
 * Helps demonstrate method overloading.
 */
public class Position {
    private int col;
    private int row;

    public Position(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int getCol() { return col; }
    public int getRow() { return row; }

    @Override
    public String toString() {
        return "(" + col + ", " + row + ")";
    }

    /**
     * Creates a new position offset from this one
     */
    public Position offset(int deltaCol, int deltaRow) {
        return new Position(col + deltaCol, row + deltaRow);
    }
}