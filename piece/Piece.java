package piece;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import main.GamePanel;
import main.Board;
import main.Position;

import javax.imageio.ImageIO;

public abstract class Piece {

    private BufferedImage image;
    private int x,y;
    private int col, row, preCol, preRow;
    private int color;


    public Piece(int color, int col, int row){
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }
    public void setX(int x){ this.x = x;}
    public void setY(int y){ this.y = y;}
    public void setCol(int col){ this.col = col;}
    public void setRow(int row){ this.row = row;}
    public void setPreCol(int precol){ this.preCol = precol; }
    public void setPreRow(int prerow){ this.preRow = prerow; }
    public void setColor(int color){ this.color = color; }
    public int getCol() { return col; }
    public int getRow() { return row; }
    public int getPreCol() { return preCol; }
    public int getPreRow() { return preRow; }
    public int getColor() { return color; }

    // Example for better exception handling in image loading
    public BufferedImage getImage(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            if (image == null) {
                throw new IOException("Image could not be loaded: " + imagePath);
            }
            return image;
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            // Create a default/placeholder image instead of returning null
            return createDefaultImage();
        } catch (Exception e) {
            System.err.println("Unexpected error loading image: " + e.getMessage());
            return createDefaultImage();
        }
    }
    /**
     * Creates a simple colored square as a fallback image.
     * Used when the actual piece image cannot be loaded.
     */
    private BufferedImage createDefaultImage() {
        // Create a simple colored square as a fallback
        BufferedImage defaultImg = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = defaultImg.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 50, 50);
        g.setColor(Color.WHITE);
        g.drawString("?", 20, 30);
        g.dispose();
        return defaultImg;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int getX(int col){
        return col * Board.SQUARE_SIZE;
    }

    public int getY(int row){
        return row * Board.SQUARE_SIZE;
    }

    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }

    public void updatePosition() {
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    public boolean canMove(int targetCol, int targetRow) {

        return false;
    }

    /**
     * Overloaded canMove method that takes a Position object.
     * Demonstrates method overloading polymorphism.
     */
    public boolean canMove(Position targetPosition) {
        return canMove(targetPosition.getCol(), targetPosition.getRow());
    }

    public boolean isWithinBoard(int targetCol, int targetRow) {
        return targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7;
    }

    // The problem with the current checkPath implementation is that it checks for ANY piece blocking the path,
// whereas it should only check squares between the start and target positions.

    // Replace the existing checkPath method with this improved version:
    public boolean checkPath(int targetCol, int targetRow) {
        // Starting position
        int startCol = this.preCol;
        int startRow = this.preRow;

        // Calculate direction of movement
        int colDirection = Integer.compare(targetCol, startCol);  // Will be -1, 0, or 1
        int rowDirection = Integer.compare(targetRow, startRow);  // Will be -1, 0, or 1

        // Start checking from the square after the starting position
        int currentCol = startCol + colDirection;
        int currentRow = startRow + rowDirection;

        // Check each square until we reach the target (exclusive)
        while (currentCol != targetCol || currentRow != targetRow) {
            // Debug message to trace path checking


            if (checkSpot(currentCol, currentRow)) {
                System.out.println("Path is blocked at " + currentCol + "," + currentRow);
                return true;  // Found a piece blocking the path
            }

            currentCol += colDirection;
            currentRow += rowDirection;
        }

        return false;  // Path is clear
    }

    /**
     * Overloaded checkPath method that takes a Position object.
     * Another example of method overloading.
     */
    public boolean checkPath(Position targetPosition) {
        return checkPath(targetPosition.getCol(), targetPosition.getRow());
    }

    /**
     * Overloaded constructor that takes a Position object.
     * This would be added to each concrete piece class as well.
     */
    public Piece(int color, Position position) {
        this(color, position.getCol(), position.getRow());
    }

    public boolean checkSpot(int targetCol, int targetRow) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece != this && piece.getCol() == targetCol && piece.getRow() == targetRow) {
                return true;  // Found a piece at target location
            }
        }
        return false;
    }

    public void draw(Graphics2D g2){
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }

    /**
     * Moves the piece by a relative offset.
     * Demonstrates coercion polymorphism by implicitly converting the double
     * parameter to int when setting the row.
     */
    public void moveByOffset(int colOffset, double rowOffset) {
        // colOffset is used as-is
        // rowOffset is implicitly converted from double to int (coercion)
        this.setCol(this.getCol() + colOffset);

        // Here the double is coerced to int - demonstrating coercion polymorphism
        this.setRow(this.getRow() + (int)rowOffset);

        // Update the position
        updatePosition();
    }

    /**
     * Another example of coercion polymorphism.
     * The float parameter is implicitly converted to int.
     */
    public void scalePosition(float scaleFactor) {
        // scaleFactor (float) is coerced to int when multiplied with getCol() and getRow()
        this.setCol((int)(this.getCol() * scaleFactor));
        this.setRow((int)(this.getRow() * scaleFactor));
        updatePosition();
    }

    // Add these exception handling examples

    /**
     * Safely attempts to move a piece to a target position.
     * Demonstrates exception handling with try-catch blocks.
     */
    public boolean tryMove(int targetCol, int targetRow) {
        try {
            // Check if target position is valid
            if (!isWithinBoard(targetCol, targetRow)) {
                throw new IllegalArgumentException("Target position outside board: " +
                        targetCol + "," + targetRow);
            }

            // Check if the piece can move to the target position
            if (!canMove(targetCol, targetRow)) {
                throw new IllegalStateException("Invalid move for " +
                        this.getClass().getSimpleName());
            }

            // If all checks pass, update position
            setCol(targetCol);
            setRow(targetRow);
            updatePosition();
            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid move attempt: " + e.getMessage());
            return false;
        } catch (IllegalStateException e) {
            System.err.println("Invalid move: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error during move: " + e.getMessage());
            return false;
        }
    }

}
