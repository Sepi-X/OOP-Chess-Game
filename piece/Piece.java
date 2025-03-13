package piece;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import main.GamePanel;
import main.Board;

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
    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;
        try{
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        }catch (IOException e){
            e.printStackTrace();
        }
        return image;
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
            System.out.println("Checking path at " + currentCol + "," + currentRow +
                    " from " + startCol + "," + startRow +
                    " to " + targetCol + "," + targetRow);

            if (checkSpot(currentCol, currentRow)) {
                System.out.println("Path is blocked at " + currentCol + "," + currentRow);
                return true;  // Found a piece blocking the path
            }

            currentCol += colDirection;
            currentRow += rowDirection;
        }

        return false;  // Path is clear
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
}
