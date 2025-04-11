package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable {

	//game window size
	private static  int WIDTH = 1100;
	private static  int HEIGHT = 800;
	//Frame Per Second
	private final int FPS = 60;
	//Manages the game loop
	Thread gameThread;

	//has a board
	Board board = new Board(); //chessboard
	//has a mouse
	Mouse mouse = new Mouse(); //Mouse Interaction

	// Pieces Storage( has pieces)
	private static ArrayList<Piece> pieces = new ArrayList<>();//stores all active pieces
	private static ArrayList<Piece> simPieces = new ArrayList<>();//copy of pieces
	public static ArrayList<Piece> getSimPieces() {
		return simPieces;
	}
	Piece activeP;//currently selected (active) piece

	// Color Constants
	private static final int WHITE = 0;
	public static int getWhite(){
		return WHITE;
	}
	private static final int BLACK = 1;
	public static int getBlack(){
		return BLACK;
	}

	int currentColor = WHITE;// keeps track of turns

	// Variables for Move Validation
	private int startCol;
	private int startRow;

	// Game state variables
	private int gameState = GameState.getOngiong(); //stores the current state
	private boolean gameOver = false; // true = game ends
	private String statusMessage = ""; //store messages

	//Constructor & Initialization
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.pink);
		// Mouse move pieces
		addMouseMotionListener(mouse);
		addMouseListener(mouse);

		setPieces(); //set pieces in further (initializes all pieces)
		copyPieces(pieces, simPieces);// copy pieces for simulate
	}

	//Launching the Game
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	//Initializing chess pieces
	public void setPieces() {
		// White team

		pieces.add(new Pawn(WHITE, new Position(0, 6)));
		pieces.add(new Pawn(WHITE, 1, 6));
		pieces.add(new Pawn(WHITE, 2, 6));
		pieces.add(new Pawn(WHITE, 3, 6));
		pieces.add(new Pawn(WHITE, 4, 6));
		pieces.add(new Pawn(WHITE, 5, 6));
		pieces.add(new Pawn(WHITE, 6, 6));
		pieces.add(new Pawn(WHITE, new Position(7, 6)));
		pieces.add(new Rook(WHITE, new Position(0, 7)));
		pieces.add(new Rook(WHITE, 7, 7));
		pieces.add(new Knight(WHITE, new Position(1, 7)));
		pieces.add(new Knight(WHITE, 6, 7));
		pieces.add(new Bishop(WHITE, new Position(2, 7)));
		pieces.add(new Bishop(WHITE, 5, 7));

		pieces.add(new Queen(WHITE, 3, 7));
		pieces.add(new King(WHITE, new Position(4, 7)));

		// Black team

		pieces.add(new Pawn(BLACK, new Position(0, 1)));
		pieces.add(new Pawn(BLACK, 1, 1));
		pieces.add(new Pawn(BLACK, 2, 1));
		pieces.add(new Pawn(BLACK, 3, 1));
		pieces.add(new Pawn(BLACK, 4, 1));
		pieces.add(new Pawn(BLACK, 5, 1));
		pieces.add(new Pawn(BLACK, 6, 1));
		pieces.add(new Pawn(BLACK, new Position(7, 1)));
		pieces.add(new Rook(BLACK, new Position(0, 0)));
		pieces.add(new Rook(BLACK, 7, 0));
		pieces.add(new Knight(BLACK, new Position(1, 0)));
		pieces.add(new Knight(BLACK, 6, 0));
		pieces.add(new Bishop(BLACK, new Position(2, 0)));
		pieces.add(new Bishop(BLACK, 5, 0));
		pieces.add(new Queen(BLACK, new Position(3, 0)));

		pieces.add(new King(BLACK, 4, 0));
	}

	//Helper method for Copying Chess Pieces(simulating moves)
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		target.clear();
		for (int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}

	//Game Loop (Thread Execution)
	@Override // from the interface runnable
	public void run() {
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;

		while (gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime)/drawInterval;
			lastTime = currentTime;

			if (delta >= 1) {
				update(); // update game state
				repaint(); // redraw the screen
				delta--; // prepare for the next frame
			}
		}
	}

	private void handlePawnPromotion(Pawn pawn) {
		String[] options = {"Queen", "Rook", "Bishop", "Knight"};
		//Displays dialog asking
		int choice = JOptionPane.showOptionDialog(
				this,
				"Choose promotion piece:",
				"Pawn Promotion",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]
		);

		//Initializes for the new promotion piece
		Piece newPiece = null;
		//current position
		int col = pawn.getCol();
		int row = pawn.getRow();
		int color = pawn.getColor();

		switch (choice) {
			case 0: // Queen
				newPiece = new Queen(color, col, row);
				break;
			case 1: // Rook
				newPiece = new Rook(color, col, row);
				break;
			case 2: // Bishop
				newPiece = new Bishop(color, col, row);
				break;
			case 3: // Knight
				newPiece = new Knight(color, col, row);
				break;
			default: // Default to Queen if dialog is closed
				newPiece = new Queen(color, col, row);
		}

		simPieces.remove(pawn); // removes the pawn
		simPieces.add(newPiece); // new promoted piece
	}

	//method continuously updates the game state
	private void update() {
		if (gameOver) {
			// Game is over, no more moves allowed
			if (mouse.isClicked()) {
				mouse.resetClick();
			}
			return;
		}

		// First, check if we need to automatically capture a king who took a protected piece
		autoCaptureFoolishKing();

		// If the game became over after auto-capturing, return
		if (gameOver) {
			return;
		}

		// Check if a king has been captured
		boolean whiteKingExists = false;
		boolean blackKingExists = false;

		//checks the king is still exist
		for (Piece p : simPieces) {
			if (p instanceof King) {
				if (p.getColor() == WHITE) {
					whiteKingExists = true;
				}
				else {
					blackKingExists = true;
				}
			}
		}

		// If Black King is missing, the game should end immediately
		if (!whiteKingExists) {
			gameState = GameState.getCheckMate();
			currentColor = BLACK; // Set black as winner
			gameOver = true;
			statusMessage = "Checkmate! Black wins!";
			return;
			//If Black King is missing, the game should end immediately
		}
		else if (!blackKingExists) {
			gameState = GameState.getCheckMate();
			currentColor = WHITE; // Set white as winner
			gameOver = true;
			statusMessage = "Checkmate! White wins!";
			return;
		}

		// If we have a new game state (check, checkmate), update the message
		if (gameState != GameState.getOngiong()) {
			updateStatusMessage();
		}

		// Process Mouse Clicks
		if (mouse.isClicked()) {
			int col = mouse.getX() / Board.SQUARE_SIZE;
			int row = mouse.getY() / Board.SQUARE_SIZE;

			// Reset the click state after processing
			mouse.resetClick();

			if (activeP == null) {
				// First click: Select a piece
				for (Piece piece : simPieces) {
					if (piece.getColor() == currentColor &&
							piece.getCol() == col &&
							piece.getRow() == row) {

						activeP = piece;
						//original poistion
						startCol = piece.getCol();
						startRow = piece.getRow();
						activeP.setPreCol(startCol);
						activeP.setPreRow(startRow);

						break;
					}
				}
			}
			else {
				// Second click: Move the selected piece
				if (activeP.getCol() == col && activeP.getRow() == row) {
					// Clicked on the same piece again, deselect it
					activeP = null;
				}
				//Check if move is valid
				else if (board.isValidPosition(col, row) && isLegalMove(activeP, col, row)) {
					// Check if the target square is occupied
					Piece pieceAtTarget = null;
					for (Piece p : simPieces) {
						if (p != activeP && p.getCol() == col && p.getRow() == row) {
							if (p.getColor() == activeP.getColor()) {
								// Clicked on another piece of same color - change selection
								activeP = p;
								startCol = p.getCol();
								startRow = p.getRow();
								activeP.setPreCol(startCol);
								activeP.setPreRow(startRow);
								return;
							}
							pieceAtTarget = p;
							break;
						}
					}


					// Special case: Check if a king is capturing a protected piece
					boolean capturedProtectedPiece = false;
					if (activeP instanceof King && pieceAtTarget != null) {
						// Check if the piece is protected
						for (Piece defender : simPieces) {
							// Skip pieces of same color as king and the piece being captured
							if (defender.getColor() == activeP.getColor() || defender == pieceAtTarget) {
								continue;
							}

							// Can this piece protect the target?
							if (defender.canMove(col, row)) {
								// For pieces that need a clear path, check if the path is clear
								if (!(defender instanceof Knight)) {
									// Check if path is blocked by another piece
									if (defender.checkPath(col, row)) {
										continue; // Path is blocked
									}
								}

								// The piece is protected! But we'll allow the king to capture it anyway
								capturedProtectedPiece = true;
								break;
							}
						}
					}

					// Remove the captured piece safely
					if (pieceAtTarget != null) {

						simPieces.remove(pieceAtTarget);// remove from the arraylist
					}

					// Move the selected piece
					activeP.setCol(col);
					activeP.setRow(row);
					activeP.setX(col * Board.SQUARE_SIZE);
					activeP.setY(row * Board.SQUARE_SIZE);
					activeP.updatePosition();

					// Check for pawn promotion
					if (activeP instanceof Pawn) {
						Pawn pawn = (Pawn) activeP;
						if (pawn.canBePromoted()) {
							handlePawnPromotion(pawn);
						}
					}

					// Switch turn(ternary operator)condition ? valueIfTrue : valueIfFalse;
					currentColor = (currentColor == WHITE) ? BLACK : WHITE;
					activeP = null; //no piece is currently selected


					// If a king captured a protected piece, we'll let the auto-capture
					// function handle it on the next update cycle - no need to
					// check for checkmate here

					// Only check for check/checkmate if we didn't just capture a protected piece
					if (!capturedProtectedPiece) {
						// to check if king is under attack
						boolean kingInCheck = GameState.isKingInCheck(simPieces, currentColor);

						if (kingInCheck) {

							// Use the improved checkmate detection
							if (isCheckmate()) {
								gameState = GameState.getCheckMate();
								gameOver = true;
								System.out.println("CHECKMATE detected!");
							}
							else {
								gameState = GameState.getCheck();
								System.out.println("CHECK - King can still escape");
							}
						}
						else {
							// Not in check, but check
							boolean hasLegalMoves = false;

							// Check if any piece has a legal move
							for (Piece p : simPieces) {
								if (p.getColor() == currentColor) {
									for (int r = 0; r < 8; r++) {
										for (int c = 0; c < 8; c++) {
											if (isLegalMove(p, c, r)) {
												hasLegalMoves = true;
												break;
											}
										}
										if (hasLegalMoves) break;
									}
									if (hasLegalMoves) break;
								}
							}

							if (hasLegalMoves) {
								gameState = GameState.getOngiong();
							}
							else {
								gameState = GameState.getStalemate();
								gameOver = true;
								System.out.println("STALEMATE - no legal moves but not in check");
							}
						}
					}

					// Update status message based on new game state
					updateStatusMessage();

					// If game is over, print message
					if (gameOver) {
						System.out.println("GAME OVER: " + (gameState == GameState.getCheckMate() ? "Checkmate" : "Stalemate"));
					}
				} else {
					// Invalid move - either clicked elsewhere on board or clicked invalid destination
					// Check if clicked on another piece of same color
					for (Piece piece : simPieces) {
						if (piece.getColor() == currentColor &&
								piece.getCol() == col &&
								piece.getRow() == row) {

							activeP = piece;
							startCol = piece.getCol();
							startRow = piece.getRow();
							activeP.setPreCol(startCol);
							activeP.setPreRow(startRow);
							return;
						}
					}

					// Clicked on empty square or opponent's piece that can't be reached - deselect
					activeP = null;
				}
			}
		}
	}

	//helper method
	private void updateStatusMessage() {
		//determines the color
		String colorName = (currentColor == WHITE) ? "White" : "Black";

		switch (gameState) {
			case 0: // GameState.ONGOING
				statusMessage = "";
				break;
			case 1: // GameState.CHECK
				statusMessage = colorName + " is in check!";
				break;
			case 2: // GameState.CHECKMATE
				String winner = (currentColor == WHITE) ? "Black" : "White";
				statusMessage = "Checkmate! " + winner + " wins!";
				break;
			case 3: // GameState.STALEMATE
				statusMessage = "Stalemate!";
				break;
			default:
				statusMessage = "";
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g); // parent class repaint the component
		Graphics2D g2 = (Graphics2D)g; // Converts Graphics to Graphics2D

		// Draw board
		board.draw(g2);

		// If a piece is selected, highlight its position and valid moves
		if (activeP != null) {
			// Highlight selected piece position with yellow
			g2.setColor(new Color(255, 255, 0, 100));  // Semi-transparent yellow
			g2.fillRect(activeP.getCol() * Board.SQUARE_SIZE, activeP.getRow() * Board.SQUARE_SIZE,
					Board.SQUARE_SIZE, Board.SQUARE_SIZE);

			// Highlight valid moves with light blue
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					if (isLegalMove(activeP, col, row)) {
						g2.setColor(new Color(100, 180, 255, 80));  // Semi-transparent light blue
						g2.fillRect(col * Board.SQUARE_SIZE, row * Board.SQUARE_SIZE,
								Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					}
				}
			}
		}

		// Highlight king in red if it's in check or checkmate
		if (gameState == GameState.getCheck() || gameState == GameState.getCheckMate()) {
			for (Piece p : simPieces) {
				if (p instanceof King && p.getColor() == currentColor) {
					g2.setColor(new Color(255, 0, 0, 100));  // Semi-transparent red
					g2.fillRect(p.getCol() * Board.SQUARE_SIZE, p.getRow() * Board.SQUARE_SIZE,
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					break;
				}
			}
		}

		// Draw all pieces
		for (Piece p : simPieces) {
			p.draw(g2);// calls for each piece
		}

		// Draw game status message
		if (!statusMessage.isEmpty()) {
			g2.setFont(new Font("Arial", Font.BOLD, 24));
			g2.setColor(Color.WHITE);

			// Draw message on the right side of the board
			int messageX = 8 * Board.SQUARE_SIZE + 20;
			int messageY = 100;

			g2.drawString(statusMessage, messageX, messageY);

			// Draw additional message for game over
			if (gameOver) {
				g2.setFont(new Font("Arial", Font.BOLD, 18));
				g2.drawString("Game Over", messageX, messageY + 40);
				g2.drawString("Restart the application", messageX, messageY + 70);
				g2.drawString("to play again", messageX, messageY + 100);
			}
		}

		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Boo Anitqua", Font.PLAIN, 40));
		if (currentColor == WHITE) {
			g2.drawString("White's turn", 840, 550);
		} else {
			g2.drawString("Black's turn", 840, 250);
		}
	}

	// in king class
	public static boolean isKingInCheck(int kingColor) {

		return GameState.isKingInCheck(simPieces, kingColor);
	}



	/**
	 * Modified isLegalMove method to allow kings to capture protected pieces,
	 * which will then result in the king being captured on the next turn
	 */
	private boolean isLegalMove(Piece piece, int targetCol, int targetRow) {
		// First, it checks if the move is valid according to the specific
		// chess piece's movement rules by calling the piece's canMove method
		if (!piece.canMove(targetCol, targetRow)) {
			return false;
		}

		// Check for same-color piece at target
		for (Piece p : simPieces) {
			if (p != piece && p.getCol() == targetCol && p.getRow() == targetRow && p.getColor() == piece.getColor()) {
				return false;
			}
		}




		// Standard check for all non-king pieces:
		// Save the original position
		int originalCol = piece.getCol();
		int originalRow = piece.getRow();

		// Find any piece at the target position to be captured
		Piece capturedPiece = null;
		for (Piece p : simPieces) {
			if (p != piece && p.getCol() == targetCol && p.getRow() == targetRow) {
				capturedPiece = p;
				break;
			}
		}

		// Create a copy of the pieces list for simulation
		ArrayList<Piece> simulatedPieces = new ArrayList<>(simPieces);

		// Temporarily remove captured piece if it exists
		if (capturedPiece != null) {
			simulatedPieces.remove(capturedPiece);
		}

		// Temporarily move the piece in our simulation
		piece.setCol(targetCol);
		piece.setRow(targetRow);

		// Check if this move would leave our king in check
		boolean kingInCheck = GameState.isKingInCheck(simulatedPieces, piece.getColor());

		// Restore the original position
		piece.setCol(originalCol);
		piece.setRow(originalRow);

		// The move is legal only if it doesn't leave the king in check
		return !kingInCheck;
	}

	/**
	 * Special method to accurately determine if the game is in checkmate
	 */
	private boolean isCheckmate() {
		// First, verify that the current player's king is in check
		boolean kingInCheck = GameState.isKingInCheck(simPieces, currentColor);
		if (!kingInCheck) {
			return false; // Not checkmate if the king isn't in check
		}

		System.out.println("Checking for checkmate - king is in check");

		// Find the king
		King king = null;
		for (Piece p : simPieces) {
			if (p instanceof King && p.getColor() == currentColor) {
				king = (King) p;
				break;
			}
		}

		if (king == null) {
			System.err.println("ERROR: King not found for color " + currentColor);
			return false;
		}

		// Check all possible king moves including captures
		int kingCol = king.getCol();
		int kingRow = king.getRow();

		// Try all 8 possible king moves
		int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
		int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

		for (int i = 0; i < 8; i++) {
			int newCol = kingCol + dx[i];
			int newRow = kingRow + dy[i];

			// Skip moves outside the board
			if (newCol < 0 || newCol > 7 || newRow < 0 || newRow > 7) {
				continue;
			}

			System.out.println("Checking if king can move to " + newCol + "," + newRow);

			// Check if this move is legal
			if (isLegalMove(king, newCol, newRow)) {
				System.out.println("King can escape check by moving to " + newCol + "," + newRow);
				return false; // King can escape, not checkmate
			}
		}

		// Check if any piece can block the check or capture the attacking piece
		for (Piece p : simPieces) {
			// Only consider current player's pieces
			if (p.getColor() != currentColor || p instanceof King) {
				continue;
			}

			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					if (isLegalMove(p, col, row)) {
						System.out.println("Piece " + p.getClass().getSimpleName() +
								" at " + p.getCol() + "," + p.getRow() +
								" can block/capture by moving to " + col + "," + row);
						return false; // A piece can block or capture, not checkmate
					}
				}
			}
		}

		// If we get here, no escape moves were found - it's checkmate
		System.out.println("CHECKMATE confirmed - no legal moves found");
		return true;
	}



	/**
	 * Add this method to your GamePanel class to automatically capture kings
	 * that have captured protected pieces
	 */
	private void autoCaptureFoolishKing() {
		// Find opponent's king
		King opponentKing = null;
		for (Piece p : simPieces) {
			if (p instanceof King && p.getColor() != currentColor) {
				opponentKing = (King)p;
				break;
			}
		}

		if (opponentKing == null)
			return;

		// Check if any of our pieces can capture the opponent's king
		for (Piece p : simPieces) {
			if (p.getColor() == currentColor && p.canMove(opponentKing.getCol(), opponentKing.getRow())) {
				// Knights don't need path checking since they can jump
				if (p instanceof Knight || !p.checkPath(opponentKing.getCol(), opponentKing.getRow())) {
					captureKing(p, opponentKing);
					break;
				}
			}
		}
	}

	// New helper method to extract functionality
	private void captureKing(Piece capturingPiece, King targetKing) {
		// Capture the king
		simPieces.remove(targetKing);

		// Move the capturing piece to the king's position
		capturingPiece.setCol(targetKing.getCol());
		capturingPiece.setRow(targetKing.getRow());
		capturingPiece.setX(targetKing.getCol() * Board.SQUARE_SIZE);
		capturingPiece.setY(targetKing.getRow() * Board.SQUARE_SIZE);
		capturingPiece.updatePosition();

		// End the game
		gameOver = true;
		gameState = GameState.getCheckMate();
		statusMessage = (currentColor == GamePanel.WHITE ? "White" : "Black") + " wins by mate!";
	}

	// Add this method to your GamePanel class



}
