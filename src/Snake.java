import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Snake extends GameActor {
	private final Color HEAD_COLOR = new Color(Integer.parseInt("215E21", 16));
	private final Color TAIL_COLOR = Color.green;

	// Defines the coordinates for each snake tile
	private Point tilePoints[] = new Point[1000];

	// Defines the number of tiles making up the snake
	private int numTiles;
	public final int INITIAL_SNAKE_TILES = 3;

	// Defines the current direction of the snake
	// This keeps track of where the snake should go next.
	private String direction;

	public Snake(int tileWidth, int tileHeight) {
		super(tileWidth, tileHeight);
		direction = "right";
		numTiles = INITIAL_SNAKE_TILES;
	}
	
	// Returns the number of tiles in the snake
	public int getNumTiles() {
	  return numTiles;
	}
	
	// Returns the direction of the snake
	public String getDirection() {
	  return direction;
	}
	
	// Set the snake direction
	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	// When snake head is directly on the apple tile,
	// apple is then hit
	public boolean hasHitApple(Apple appleTile) {
		return ((tilePoints[0].x == appleTile.getXPos())
			     && (tilePoints[0].y == appleTile.getYPos()));
	}
	
	// Check if the snake ran into itself
	public boolean hasHitSelf() {
		for (int ind = this.numTiles; ind > 0; ind--) {
			if ((ind > INITIAL_SNAKE_TILES) && (tilePoints[0].x == tilePoints[ind].x)
					&& (tilePoints[0].y == tilePoints[ind].y)) {
				return true;
			}
		}
		return false;
	}
	
	// Check if snake ran into the boundaries of the game board
	// Game is over if the snake head are outside board bounds
	public boolean hasHitBounds(int width, int height) {
		if (tilePoints[0].x > width) {
			return true;
		} else if (tilePoints[0].x < 0) {
			return true;
		} else if (tilePoints[0].y < 0) {
			return true;
		} else if (tilePoints[0].y > height) {
			return true;
		}
		return false;
	}
	
	// Moves the snake in the current direction
	public void move() {
		// First since a snake slides across the tiles,
		// move each tile so that every tile is now equal
		// to the previous tile (to move all tiles).
		for (int ind = this.numTiles; ind > 0; ind--) {
			tilePoints[ind].x = tilePoints[(ind - 1)].x;
			tilePoints[ind].y = tilePoints[(ind - 1)].y;
		}
		
		// Based on the current direction, we need to
		// move the head tile towards the next tile in that direction...
		if (direction == "left") {
			tilePoints[0].x -= this.tileWidth;
		} else if (direction == "right") {
			tilePoints[0].x += this.tileWidth;
		} else if (direction == "up") {
			tilePoints[0].y -= this.tileHeight;
		} else if (direction == "down") {
			tilePoints[0].y += this.tileHeight;
		}
	}
	
	// Set the starting position for a snake given coords
	public void setStartPos(int xStart, int yStart) {
		Point currentSnakeTile;
		for (int ind = 0; ind <= this.numTiles; ind++) {
			currentSnakeTile = tilePoints[ind] = new Point();
			currentSnakeTile.x = xStart - (ind * tileWidth);
			currentSnakeTile.y = yStart;
		}
	}

	// Grow the snake tail by one tile
	public void growTail() {
		this.numTiles++;
		tilePoints[this.numTiles] = new Point();
	}
	
	// Paint snake as a series of squares based on tile coordinates
	public void paintActor(Graphics g2d) {
		Point currentSnakeTile;
		for (int ind = 0; ind < getNumTiles(); ind++) {
			currentSnakeTile = tilePoints[ind];
			// Paint the head with a special color...
			if (ind == 0) {
				// Paint the square
				g2d.setColor(HEAD_COLOR);
				g2d.fillRect(currentSnakeTile.x, currentSnakeTile.y,
						this.tileWidth, this.tileHeight);
				// And paint a border
				g2d.setColor(HEAD_COLOR);
				g2d.drawRect(currentSnakeTile.x, currentSnakeTile.y,
						this.tileWidth, this.tileHeight);
			} else {
				// Paint the square
				g2d.setColor(TAIL_COLOR);
				g2d.fillRect(currentSnakeTile.x, currentSnakeTile.y,
						this.tileWidth, this.tileHeight);
				// And paint a border
				g2d.setColor(HEAD_COLOR);
				g2d.drawRect(currentSnakeTile.x, currentSnakeTile.y,
						this.tileWidth, this.tileHeight);
			}
		}
	}
}
