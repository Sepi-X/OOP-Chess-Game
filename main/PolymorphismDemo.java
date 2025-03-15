package main;

import piece.*;
import java.util.ArrayList;

/**
 * This class explicitly demonstrates dynamic binding (inclusion polymorphism)
 * as requested by Professor Distefano.
 */
public class PolymorphismDemo {

    /**
     * Main demonstration method that shows how different pieces respond differently
     * to the same method call (canMove) based on their actual runtime type.
     */
    public static void demonstrateDynamicBinding() {
        System.out.println("===== DYNAMIC BINDING DEMONSTRATION =====");

        // Create an array of Piece references holding different concrete piece types
        Piece[] pieces = new Piece[6];
        pieces[0] = new Rook(GamePanel.WHITE, 0, 0);
        pieces[1] = new Knight(GamePanel.WHITE, 1, 0);
        pieces[2] = new Bishop(GamePanel.WHITE, 2, 0);
        pieces[3] = new Queen(GamePanel.WHITE, 3, 0);
        pieces[4] = new King(GamePanel.WHITE, 4, 0);
        pieces[5] = new Pawn(GamePanel.WHITE, 5, 0);

        // Demonstrate dynamic binding by calling the same method on different objects
        for (Piece piece : pieces) {
            // The specific implementation called depends on the actual runtime type
            boolean canMoveToCenter = piece.canMove(3, 3);

            // Output which implementation was called based on the actual object type
            System.out.println(piece.getClass().getSimpleName() +
                    " can move to center: " + canMoveToCenter);
        }

        System.out.println("\nThis demonstrates how the same 'canMove' method call");
        System.out.println("performs different actions depending on the actual piece type,");
        System.out.println("even though all references have the same static type (Piece).");
    }

    /**
     * Call this method from main.java to demonstrate the concept
     */
    public static void runDemo() {
        demonstrateDynamicBinding();
    }
}