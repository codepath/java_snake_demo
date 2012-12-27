package codepath.snake;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class AppleActor extends GameActor {
	// Defines the color of the apple
	private final Color BORDER_COLOR = new Color(Integer.parseInt("8C1717", 16));
	private final Color DRAW_COLOR = Color.red;

	// Defines the coordinate for the apple
	private Point tilePoint = new Point();

	public AppleActor(int tileWidth, int tileHeight) {
		super(tileWidth, tileHeight);
	}
	
	// Returns the x position of the apple tile
	public int getXPos() {
	  return this.tilePoint.x;
	}
	
	// Returns the y position of the apple tile
	public int getYPos() {
      return this.tilePoint.y;
    }
	
	// Move the apple to a new spot on the board
	// Generate a random position and set the new coordinates for apple
	public void reposition(int multiplier) {
		// Generate a random x coordinate for next apple
		int randomCoordinate = (int) (Math.random() * multiplier);
		tilePoint.x = ((randomCoordinate * this.tileWidth));
		// Generate a random y coordinate for next apple
		randomCoordinate = (int) (Math.random() * multiplier);
		tilePoint.y = ((randomCoordinate * this.tileHeight));
	}
	
	// Paint the apple on the board
	public void paintActor(Graphics g) {
		// Paint apple as an oval
		g.setColor(DRAW_COLOR);
		g.fillOval(tilePoint.x, tilePoint.y, this.tileWidth, this.tileHeight);
		g.setColor(BORDER_COLOR);
		g.drawOval(tilePoint.x, tilePoint.y, this.tileWidth, this.tileHeight);
	}
}
