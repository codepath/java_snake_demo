package codepath.snake.actors;
import java.awt.Graphics;

// Represents an 'actor' in the game, which can be anything on the board
// The snake and the apple are actors
public abstract class GameActor {
	protected int tileWidth;
	protected int tileHeight;
	
	public GameActor(int tileWidth, int tileHeight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}
	
	public abstract void paintActor(Graphics g);
}
