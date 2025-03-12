package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {
	public int x, y;
	public boolean clicked;

	@Override
	public void mousePressed(MouseEvent e) {
		// When mouse is pressed, record position but don't set clicked yet
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Set clicked to true only on release and at the same position
		x = e.getX();
		y = e.getY();
		clicked = true;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// Update position during drag but don't trigger click
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Update position during movement
		x = e.getX();
		y = e.getY();
	}

	// Method to reset the click state after it's been processed
	public void resetClick() {
		clicked = false;
	}
}