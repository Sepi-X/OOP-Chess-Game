package main;

import piece.Piece;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Utility class demonstrating parametric polymorphism through generics.
 * This allows type-safe operations on chess pieces.
 */
public class ChessUtils {

    /**
     * Generic method that filters pieces based on a predicate.
     * Demonstrates parametric polymorphism by working with any subtype of Piece.
     *
     * @param <T> Type parameter constrained to Piece subtypes
     * @param pieces List of pieces to filter
     * @param predicate Condition to test each piece against
     * @return Filtered list containing only pieces that satisfy the predicate
     */
    public static <T extends Piece> ArrayList<T> filterPieces(ArrayList<T> pieces, Predicate<T> predicate) {
        ArrayList<T> result = new ArrayList<>();
        for (T piece : pieces) {
            if (predicate.test(piece)) {
                result.add(piece);
            }
        }
        return result;
    }

    /**
     * Generic method that moves all pieces by a specified delta.
     * Works with any collection of pieces.
     *
     * @param <T> Type parameter constrained to Piece subtypes
     * @param pieces Collection of pieces to move
     * @param deltaCol Column offset
     * @param deltaRow Row offset
     */
    public static <T extends Piece> void movePieces(ArrayList<T> pieces, int deltaCol, int deltaRow) {
        for (T piece : pieces) {
            piece.setCol(piece.getCol() + deltaCol);
            piece.setRow(piece.getRow() + deltaRow);
            piece.updatePosition();
        }
    }

    /**
     * Generic method to find pieces in a certain position range.
     *
     * @param <T> Type parameter constrained to Piece subtypes
     * @param pieces Collection to search through
     * @param minCol Minimum column (inclusive)
     * @param maxCol Maximum column (inclusive)
     * @param minRow Minimum row (inclusive)
     * @param maxRow Maximum row (inclusive)
     * @return List of pieces within the specified range
     */
    public static <T extends Piece> ArrayList<T> findPiecesInRange(
            ArrayList<T> pieces, int minCol, int maxCol, int minRow, int maxRow) {

        return filterPieces(pieces, piece ->
                piece.getCol() >= minCol && piece.getCol() <= maxCol &&
                        piece.getRow() >= minRow && piece.getRow() <= maxRow
        );
    }
}