package piece;

import main.GamePanel;

public class Rook extends Piece {
    private boolean hasMoved = false;

    public Rook(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.WHITE) {
            setImage(getImage("/piece/w-rook")) ;
        }
        else {
            setImage(getImage("/piece/b-rook")) ;
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // First check if the target position is within the board
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }

        // Check if the move is horizontal or vertical
        boolean isValidMove = (targetCol == getPreCol() || targetRow == getPreRow());
        if (!isValidMove) {
            return false;
        }

        // Check for piece of same color at target
        for (Piece p : GamePanel.simPieces) {
            if (p != this && p.getCol() == targetCol && p.getRow() == targetRow && p.getColor() == this.getColor()) {
                return false;
            }
        }

        // Check if path is clear
        return !checkPath(targetCol, targetRow);
    }

    @Override
    public void updatePosition() {
        super.updatePosition();

        // Mark the rook as moved after updating position
        if (!hasMoved) {
            hasMoved = true;
        }
    }

    public boolean hasMoved() {
        return hasMoved;
    }
}