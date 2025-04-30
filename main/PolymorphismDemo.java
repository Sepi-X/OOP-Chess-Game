package main;

import piece.*;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * This class explicitly demonstrates dynamic binding (inclusion polymorphism)
 * as requested by Professor Distefano.
 */
public final class PolymorphismDemo {

    /**
     * Main demonstration method that shows how different pieces respond differently
     * to the same method call (canMove) based on their actual runtime type.
     */
    private static void demonstrateDynamicBinding() {
        System.out.println("===== DYNAMIC BINDING DEMONSTRATION =====");

        // Create an array of Piece references holding different concrete piece types
        Piece[] pieces = new Piece[6];
        pieces[0] = new Rook(GamePanel.getWhite(), new Position(0,0));
        pieces[1] = new Knight(GamePanel.getWhite(), 1, 0);
        pieces[2] = new Bishop(GamePanel.getWhite(), 2, 0);
        pieces[3] = new Queen(GamePanel.getWhite(), 3, 0);
        pieces[4] = new King(GamePanel.getWhite(), 4, 0);
        pieces[5] = new Pawn(GamePanel.getWhite(), 5, 0);

        // Demonstrate dynamic binding by calling the same method on different objects(overriding)
        for (Piece piece : pieces) {//upcasting
            // The specific implementation called depends on the actual runtime type
            boolean canMoveToCenter = piece.canMove(3, 3);//overriding the method

            // Output which implementation was called based on the actual object type
            System.out.println(piece.getClass().getSimpleName() + " can move to center: " + canMoveToCenter);
        }

        System.out.println("\nThis demonstrates how the same 'canMove' method call");
        System.out.println("performs different actions depending on the actual piece type,");
    }

    private static void demonstrateParametricPolymorphism() {
        System.out.println("===== PARAMETRIC POLYMORPHISM DEMONSTRATION =====");

        // Create a list with different piece types
        ArrayList<Piece> pieces = new ArrayList<>();
        pieces.add(new Rook(GamePanel.getWhite(), new Position(0,0)));
        //pieces.add(new Knight(GamePanel.getWhite(), 1, 0));
        pieces.add(new Bishop(GamePanel.getWhite(), 2, 0));
        pieces.add(new Queen(GamePanel.getWhite(), 3, 0));
        //pieces.add(new King(GamePanel.getWhite(), 4, 0));
        //pieces.add(new Pawn(GamePanel.getWhite(), 5, 0));

        // Filter only pieces that can move to position (3,3)
        ArrayList<Piece> filteredPieces = ChessUtils.filterPieces(pieces, new Predicate<Piece>() {
            @Override
            public boolean test(Piece piece) {
                return piece.canMove(3, 3);
            }
        });
        System.out.println("Out of " + pieces.size() + " pieces, " +
                filteredPieces.size() + " can move to position (3,3)");
    }
    /**
     * Call this method from main.java to demonstrate the concept
     */
    public static void runDemo() {
        demonstrateDynamicBinding();
        demonstrateParametricPolymorphism();
    }


}
