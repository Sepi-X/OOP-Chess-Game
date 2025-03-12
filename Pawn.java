package piece;

import main.GamePanel;

public class Pawn extends Piece {
    private boolean hasMoved = false;

    public Pawn(int color, int col, int row) {
        super(color, col, row);
        if (color == GamePanel.WHITE) {
            setImage(getImage("/piece/w-pawn"));
        } else {
            setImage(getImage("/piece/b-pawn"));
        }
    }

    public boolean canBePromoted() {
        return (getColor() == GamePanel.WHITE && getRow() == 0) || (getColor() == GamePanel.BLACK && getRow() == 7);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // First check if the target position is within the board
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }

        // Check for piece at target position
        boolean isTargetOccupied = false;
        boolean isTargetEnemy = false;

        for (Piece piece : GamePanel.simPieces) {
            if (piece != this && piece.getCol() == targetCol && piece.getRow() == targetRow) {
                isTargetOccupied = true;
                isTargetEnemy = (piece.getColor() != this.getColor());
                break;
            }
        }

        // White pawns move up (decreasing row), black pawns move down (increasing row)
        if (getColor() == GamePanel.WHITE) {
            // Diagonal capture for white
            if (Math.abs(targetCol - getPreCol()) == 1 && targetRow == getPreRow() - 1) {
                return isTargetOccupied && isTargetEnemy;
            }

            // Forward movement for white
            if (targetCol == getPreCol()) {
                // Can't move forward if blocked
                if (isTargetOccupied) {
                    return false;
                }

                // One square forward
                if (targetRow == getPreRow() - 1) {
                    return true;
                }

                // Two squares forward from starting position
                if (!hasMoved && targetRow == getPreRow() - 2) {
                    // Check if path is clear
                    for (Piece piece : GamePanel.simPieces) {
                        if (piece != this && piece.getCol() == targetCol && piece.getRow() == getPreRow() - 1) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        } else {  // Black pawn movement
            // Diagonal capture for black
            if (Math.abs(targetCol - getPreCol()) == 1 && targetRow == getPreRow() + 1) {
                return isTargetOccupied && isTargetEnemy;
            }

            // Forward movement for black
            if (targetCol == getPreCol()) {
                // Can't move forward if blocked
                if (isTargetOccupied) {
                    return false;
                }

                // One square forward
                if (targetRow == getPreRow() + 1) {
                    return true;
                }

                // Two squares forward from starting position
                if (!hasMoved && targetRow == getPreRow() + 2) {
                    // Check if path is clear
                    for (Piece piece : GamePanel.simPieces) {
                        if (piece != this && piece.getCol() == targetCol && piece.getRow() == getPreRow() + 1) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void updatePosition() {
        super.updatePosition();
        if (!hasMoved) {
            hasMoved = true;
        }
    }
}