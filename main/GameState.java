package main;

import java.util.ArrayList;
import piece.King;
import piece.Knight;
import piece.Piece;
import piece.Rook;

public class GameState {
    public static final int ONGOING = 0;
    public static final int CHECK = 1;
    public static final int CHECKMATE = 2;
    public static final int STALEMATE = 3;



    /**
     * Enhanced method to check if the king of the specified color is in check
     * This version safely handles collections to avoid ConcurrentModificationException
     */
    public static boolean isKingInCheck(ArrayList<Piece> pieces, int kingColor) {
        // Find the king of the specified color
        King king = null;
        for (Piece p : pieces) {
            if (p instanceof King && p.getColor() == kingColor) {
                king = (King) p;
                break;
            }
        }

        if (king == null) {
            System.err.println("ERROR: King not found for color " + kingColor);
            return false; // Should never happen in a valid game
        }

        int kingCol = king.getCol();
        int kingRow = king.getRow();

        System.out.println("Checking if king at " + kingCol + "," + kingRow + " is in check");

        // Check if any opponent's piece can capture the king
        // Create a copy of the list to safely iterate
        ArrayList<Piece> piecesCopy = new ArrayList<>(pieces);

        for (Piece attacker : piecesCopy) {
            // Skip pieces of our own color
            if (attacker.getColor() == kingColor) {
                continue;
            }

            // Check if this piece can move to the king's position
            if (attacker.canMove(kingCol, kingRow)) {
                // Knights don't need path checking
                if (attacker instanceof Knight) {
                    System.out.println("King is in check by knight at " +
                            attacker.getCol() + "," + attacker.getRow());
                    return true;
                }

                // For all other pieces, check if the path to the king is clear
                boolean pathBlocked = false;

                // Calculate direction from attacker to king
                int colDirection = Integer.compare(kingCol, attacker.getCol());
                int rowDirection = Integer.compare(kingRow, attacker.getRow());

                // Start checking from the square after the attacker
                int currentCol = attacker.getCol() + colDirection;
                int currentRow = attacker.getRow() + rowDirection;

                // Check each square until we reach the king (exclusive)
                while (currentCol != kingCol || currentRow != kingRow) {
                    // Check if any piece is on this square
                    for (Piece blockingPiece : piecesCopy) {
                        if (blockingPiece.getCol() == currentCol && blockingPiece.getRow() == currentRow) {
                            pathBlocked = true;
                            break;
                        }
                    }

                    if (pathBlocked) {
                        break;
                    }

                    currentCol += colDirection;
                    currentRow += rowDirection;
                }

                // If path is not blocked, the king is in check
                if (!pathBlocked) {
                    System.out.println("King is in check by " +
                            attacker.getClass().getSimpleName() +
                            " at " + attacker.getCol() + "," + attacker.getRow());
                    return true;
                }
            }
        }

        return false;
    }

}
