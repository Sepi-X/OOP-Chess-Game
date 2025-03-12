package piece;

import main.GamePanel;

public class Knight extends Piece {
    public Knight(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.WHITE) {
            setImage(getImage("/piece/w-knight"));
        }
        else {
            setImage(getImage("/piece/b-knight"));
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // First check if the target position is within the board
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }

        // Check for piece of same color at target
        for (Piece p : GamePanel.simPieces) {
            if (p != this && p.getCol() == targetCol && p.getRow() == targetRow && p.getColor() == this.getColor()) {
                return false;
            }
        }

        // Knight moves in an L-shape: 2 squares in one direction and 1 square perpendicular
        int rowDiff = Math.abs(targetRow - getPreRow());
        int colDiff = Math.abs(targetCol - getPreCol());

        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
}