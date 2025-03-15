
package piece;

import main.GamePanel;
import main.Position;

public class Bishop extends Piece {
    public Bishop(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.WHITE) {
            setImage(getImage("/piece/w-bishop"));
        }
        else {
            setImage(getImage("/piece/b-bishop")) ;
        }
    }
    public Bishop(int color, Position position) {
        super(color, position);
        if (color == GamePanel.WHITE) {
            setImage(getImage("/piece/w-bishop"));
        }
        else {
            setImage(getImage("/piece/b-bishop"));
        }
    }


    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // First check if the target position is within the board
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }

        // Check if the move is a valid diagonal
        boolean isValidMove = Math.abs(targetRow - getPreRow()) == Math.abs(targetCol - getPreCol());
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
}
