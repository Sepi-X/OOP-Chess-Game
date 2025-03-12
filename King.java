package piece;

import main.GamePanel;

public class King extends Piece {
    private boolean hasMoved = false;

    public King(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.WHITE) {
            setImage(getImage("/piece/w-king"));
        }
        else {
            setImage(getImage("/piece/b-king"));
        }
    }
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // First check if the target position is within the board
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }

        // Calculate the absolute differences in column and row from the original position
        int colDiff = Math.abs(targetCol - getPreCol());
        int rowDiff = Math.abs(targetRow - getPreRow());

        // Check for piece of same color at target
        for (Piece p : GamePanel.simPieces) {
            if (p != this && p.getCol() == targetCol && p.getRow() == targetRow && p.getColor() == this.getColor()) {
                return false;
            }
        }

        // Check for castling (king moves two squares horizontally)
        if (!hasMoved && rowDiff == 0 && colDiff == 2) {
            // Verify it's a castling move (king moves two squares horizontally)
            return canCastle(targetCol);
        }

        // A king can move one square in any direction
        // This means either colDiff or rowDiff can be 1, or both can be 1 (for diagonal)
        // But neither can be greater than 1
        return colDiff <= 1 && rowDiff <= 1 && (colDiff != 0 || rowDiff != 0);
    }

    private boolean canCastle(int targetCol) {
        // Check if king is in check
        if (GamePanel.isKingInCheck(this.getColor())) {
            return false;
        }

        // Determine if it's kingside or queenside castling
        boolean isKingSideCastling = targetCol > getPreCol();
        int rookCol = isKingSideCastling ? 7 : 0;

        // Find the rook for castling
        Rook rook = null;
        for (Piece p : GamePanel.simPieces) {
            if (p instanceof Rook && p.getColor() == this.getColor() && p.getCol() == rookCol && p.getRow() == this.getRow()) {
                rook = (Rook) p;
                break;
            }
        }

        // Check if rook exists and hasn't moved
        if (rook == null || rook.hasMoved()) {
            return false;
        }

        // Check if path between king and rook is clear
        int startCol = Math.min(getPreCol(), rookCol) + 1;
        int endCol = Math.max(getPreCol(), rookCol);
        for (int col = startCol; col < endCol; col++) {
            if (checkSpot(col, getPreRow())) {
                return false;
            }
        }

        // Check if the king passes through or ends up in check
        int direction = isKingSideCastling ? 1 : -1;
        for (int i = 1; i <= 2; i++) {
            int checkCol = getPreCol() + (direction * i);

            // Check if this intermediate square is under attack
            for (Piece attacker : GamePanel.simPieces) {
                if (attacker.getColor() != this.getColor() && attacker.canMove(checkCol, getPreRow())) {
                    // For pieces that need a clear path, check if the path is clear
                    if (!(attacker instanceof Knight) && attacker.checkPath(checkCol, getPreRow())) {
                        continue; // Path is blocked
                    }
                    return false; // Square is under attack
                }
            }
        }

        return true;
    }

    public void performCastling(int targetCol) {
        // Determine if it's kingside or queenside castling
        boolean isKingSideCastling = targetCol > getPreCol();
        int rookCol = isKingSideCastling ? 7 : 0;
        int newRookCol = isKingSideCastling ? targetCol - 1 : targetCol + 1;

        // Find the rook for castling
        for (Piece p : GamePanel.simPieces) {
            if (p instanceof Rook && p.getColor() == this.getColor() && p.getCol() == rookCol && p.getRow() == this.getRow()) {
                // Move the rook
                p.setCol(newRookCol);
                p.setX(p.getX(newRookCol));
                p.updatePosition();
                break;
            }
        }
    }

    @Override
    public void updatePosition() {
        // Check if this is a castling move
        if (!hasMoved && Math.abs(getCol() - getPreCol()) == 2) {
            performCastling(getCol());
        }

        super.updatePosition();

        // Mark the king as moved after updating position
        if (!hasMoved) {
            hasMoved = true;
        }
    }

}