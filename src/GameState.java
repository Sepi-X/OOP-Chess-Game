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

    // Flag to prevent recursive debugging output
    private static boolean isDebugging = false;


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

    /**
     * Checks if a square is attacked by any piece of the opposing color
     * This is critical for correctly identifying if a king is in check
     * and for preventing kings from moving into check
     */
    public static boolean isSquareAttacked(ArrayList<Piece> pieces, int col, int row, int defenderColor) {
        for (Piece attacker : pieces) {
            // Only consider pieces of the opposite color
            if (attacker.getColor() == defenderColor) {
                continue;
            }

            // Can this piece attack the square?
            if (attacker.canMove(col, row)) {
                // For pieces that need a clear path, check if the path is clear
                if (!(attacker instanceof Knight) && attacker.checkPath(col, row)) {
                    continue; // Path is blocked
                }

                // Added debug output to help understand attack detection
                System.out.println("Square " + col + "," + row + " is attacked by " +
                        attacker.getClass().getSimpleName() + " at " +
                        attacker.getCol() + "," + attacker.getRow());
                return true;
            }
        }
        return false;
    }

    /**
     * Special method to check if a square is protected by any piece
     * This is specifically for preventing kings from capturing protected pieces
     */
    public static boolean isSquareProtected(ArrayList<Piece> pieces, int col, int row, int attackerColor, Piece excludePiece) {
        for (Piece defender : pieces) {
            // Only consider pieces of the opposite color and not the excluded piece
            if (defender.getColor() == attackerColor || defender == excludePiece) {
                continue;
            }

            // Can this piece move to the square? If so, it's protecting it
            if (defender.canMove(col, row)) {
                // For pieces that need a clear path, check if the path is clear
                if (!(defender instanceof Knight) && defender.checkPath(col, row)) {
                    continue; // Path is blocked
                }

                System.out.println("Square " + col + "," + row + " is protected by " +
                        defender.getClass().getSimpleName() + " at " +
                        defender.getCol() + "," + defender.getRow());
                return true;
            }
        }
        return false;
    }

    /**
     * Determines the game state: ongoing, check, checkmate, or stalemate
     */
    public static int getGameState(ArrayList<Piece> pieces, int currentColor) {
        boolean isInCheck = isKingInCheck(pieces, currentColor);
        boolean hasLegalMove = hasLegalMove(pieces, currentColor);

        // Only print debug info if not already in a debugging loop
        if (!isDebugging) {
            System.out.println((currentColor == 0 ? "White" : "Black") +
                    " is in check: " + isInCheck +
                    ", has legal moves: " + hasLegalMove);
        }

        // If not in check and no legal moves, it's stalemate
        // If in check and no legal moves, it's checkmate
        if (!hasLegalMove) {
            return isInCheck ? CHECKMATE : STALEMATE;
        }

        // If in check but has legal moves, it's just check
        if (isInCheck) {
            return CHECK;
        }

        // Otherwise, the game is ongoing
        return ONGOING;
    }

    /**
     * Debug method to list all possible moves for a player
     */
    public static void printAllPossibleMoves(ArrayList<Piece> pieces, int currentColor) {
        if (isDebugging) return; // Prevent recursive debugging

        isDebugging = true;
        try {
            System.out.println("Possible moves for " + (currentColor == 0 ? "White" : "Black") + ":");

            for (Piece p : pieces) {
                if (p.getColor() == currentColor) {
                    System.out.println("  Piece at (" + p.getCol() + "," + p.getRow() + ") type: " + p.getClass().getSimpleName());

                    boolean hasMoves = false;
                    for (int row = 0; row < 8; row++) {
                        for (int col = 0; col < 8; col++) {
                            if (isLegalMove(pieces, p, col, row)) {
                                System.out.println("    Can legally move to (" + col + "," + row + ")");
                                hasMoves = true;
                            }
                        }
                    }

                    if (!hasMoves) {
                        System.out.println("    No legal moves for this piece");
                    }
                }
            }
        } finally {
            isDebugging = false; // Make sure to reset this flag
        }
    }

    /**
     * Checks if the current player has any legal move
     */
    private static boolean hasLegalMove(ArrayList<Piece> pieces, int currentColor) {
        // Check if king is in check to ensure all pieces can try to resolve it
        boolean isInCheck = isKingInCheck(pieces, currentColor);

        // Try every possible move for every piece of the current color
        for (Piece p : pieces) {
            if (p.getColor() == currentColor) {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        if (isLegalMove(pieces, p, col, row)) {
                            return true;
                        }
                    }
                }
            }
        }

        // If we've tried all moves and none work, there are no legal moves
        return false;
    }

    /**
     * Creates a deep copy of the piece list for simulation
     */
    private static ArrayList<Piece> clonePieceList(ArrayList<Piece> original) {
        ArrayList<Piece> copy = new ArrayList<>();

        for (Piece p : original) {
            Piece clonedPiece = null;

            // Create appropriate piece type based on class
            try {
                // Create a new instance of the same class
                clonedPiece = p.getClass().getDeclaredConstructor(int.class, int.class, int.class)
                        .newInstance(p.getColor(), p.getCol(), p.getRow());

                // Set all necessary properties
                clonedPiece.setPreCol(p.getPreCol());
                clonedPiece.setPreRow(p.getPreRow());

                copy.add(clonedPiece);
            } catch (Exception e) {
                System.err.println("Error cloning piece: " + e.getMessage());
            }
        }

        return copy;
    }

    /**
     * Checks if a move is legal using complete simulation
     */
    public static boolean isLegalMove(ArrayList<Piece> pieces, Piece piece, int targetCol, int targetRow) {
        // Basic validation - can the piece move to this position according to its rules?
        if (!piece.canMove(targetCol, targetRow)) {
            return false;
        }

        // Special handling for kings
        if (piece instanceof King) {
            // Check if the king is trying to capture a piece
            Piece targetPiece = null;
            for (Piece p : pieces) {
                if (p != piece && p.getCol() == targetCol && p.getRow() == targetRow) {
                    targetPiece = p;
                    break;
                }
            }

            // If king is capturing an opponent's piece, check if it's protected by another piece
            if (targetPiece != null && targetPiece.getColor() != piece.getColor()) {
                if (isSquareProtected(pieces, targetCol, targetRow, piece.getColor(), targetPiece)) {
                    System.out.println("King cannot capture protected piece at " + targetCol + "," + targetRow);
                    return false;
                }
            }
            // For non-capture moves, check if the target square is under attack
            else if (targetPiece == null && isSquareAttacked(pieces, targetCol, targetRow, piece.getColor())) {
                System.out.println("King cannot move to attacked square at " + targetCol + "," + targetRow);
                return false;
            }
        }

        // Save the original position
        int originalCol = piece.getCol();
        int originalRow = piece.getRow();

        // Create a deep copy of pieces for simulation
        ArrayList<Piece> simulatedPieces = clonePieceList(pieces);

        // Find the piece and any potential capture in our simulated board
        Piece simulatedPiece = null;
        Piece capturedPiece = null;

        for (Piece p : simulatedPieces) {
            // Find our piece
            if (p.getClass() == piece.getClass() && p.getColor() == piece.getColor() &&
                    p.getCol() == originalCol && p.getRow() == originalRow) {
                simulatedPiece = p;
            }

            // Find any piece at the target position
            if (p.getCol() == targetCol && p.getRow() == targetRow) {
                capturedPiece = p;
            }

            // Stop if we found both
            if (simulatedPiece != null && (capturedPiece != null || (targetCol != originalCol || targetRow != originalRow))) {
                break;
            }
        }

        // If we can't find our piece in the simulation, something went wrong
        if (simulatedPiece == null) {
            return false;
        }

        // Remove captured piece if it exists and isn't our own piece
        if (capturedPiece != null) {
            if (capturedPiece.getColor() == piece.getColor()) {
                return false; // Can't capture our own piece
            }
            simulatedPieces.remove(capturedPiece);
        }

        // Make the move in our simulation
        simulatedPiece.setCol(targetCol);
        simulatedPiece.setRow(targetRow);

        // Check if this move would leave our king in check
        boolean kingInCheck = isKingInCheck(simulatedPieces, piece.getColor());

        // The move is legal only if it doesn't leave the king in check
        return !kingInCheck;
    }

    /**
     * Checks if castling is legal by simulating the entire move
     */
    public static boolean isCastlingLegal(ArrayList<Piece> pieces, King king, int targetCol) {
        // Create a temporary deep copy of the board to simulate the move
        ArrayList<Piece> simulatedPieces = clonePieceList(pieces);

        // Find king in the simulated board
        King simulatedKing = null;
        for (Piece p : simulatedPieces) {
            if (p instanceof King && p.getColor() == king.getColor()) {
                simulatedKing = (King) p;
                break;
            }
        }

        if (simulatedKing == null) return false;

        // Get the original king position
        int origCol = king.getCol();
        int origRow = king.getRow();

        // Determine castling direction
        boolean isKingSideCastling = targetCol > origCol;
        int rookCol = isKingSideCastling ? 7 : 0;
        int newRookCol = isKingSideCastling ? targetCol - 1 : targetCol + 1;

        // Check if the squares the king passes through are under attack
        int direction = isKingSideCastling ? 1 : -1;
        for (int i = 0; i <= 2; i++) {  // Check current, intermediate and final position
            int checkCol = origCol + (direction * i);
            if (isSquareAttacked(simulatedPieces, checkCol, origRow, king.getColor())) {
                return false;
            }
        }

        // Simulate king move
        simulatedKing.setCol(targetCol);

        // Find and simulate rook move
        for (Piece p : simulatedPieces) {
            if (p instanceof Rook && p.getColor() == king.getColor() && p.getCol() == rookCol && p.getRow() == origRow) {
                p.setCol(newRookCol);
                break;
            }
        }

        // Final check - is king in check after castling?
        return !isKingInCheck(simulatedPieces, king.getColor());
    }
}