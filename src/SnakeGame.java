import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

// Creates a SnakeGame class that extends JPanel
// JPanel is a component that acts as a canvas for drawing within the JFrame container
@SuppressWarnings("serial")
public class SnakeGame extends JPanel implements ActionListener {
	// Sets the width and height of the canvas to 300x300
	private final int WIDTH = 600;
	private final int HEIGHT = 600;

	// Defines the width of the actors (objects) painted on the board
	private final int TILE_WIDTH = 25;
	private final int TILE_HEIGHT = 25;

	// Define the delay between 'ticks' of the timer
	// Timer will trigger actionPerformed method every 100ms
	private final int DELAY = 120;

	// Setup colors for drawing snake
	private final Color SNAKE_HEAD_COLOR = new Color(Integer.parseInt("215E21", 16));
	private final Color SNAKE_TAIL_COLOR = Color.green;
	private final Color APPLE_COLOR = Color.red;

	// Defines the total number of possible snake tiles on the board
	// Each tile is 10x10 and the board is 300x300, so that makes for 900
	// possible tiles
	private final int TILE_COUNT = (WIDTH * HEIGHT) / (TILE_WIDTH * TILE_HEIGHT);

	// Defines the coordinates for each snake tile
	private Point snakeTiles[] = new Point[TILE_COUNT];

	// Defines the coordinate for the apple
	private Point appleTile = new Point();

	// Defines the number of tiles making up the snake
	private int numSnakeTiles;
	private final int INITIAL_SNAKE_TILES = 3;
	
	// Defines the current direction of the snake
	// This keeps track of where the snake should go next.
	private String direction;
	
	// Keeps track of if the game is currently running
	// Game stops when a game over condition is reached (i.e snake hits the wall)
	private boolean isGameRunning;
	
	// Creates a timer which 'ticks' and will trigger
	// the actionPerformed method every X interval
	// This is how the game progresses, the snake moves in 
	// the specified direction every 'tick' of the timer.
	private Timer timer;

	// Constructor for the SnakeGame Panel
	// Creates game board and starts game timer
	public SnakeGame() {
		setBackground(Color.lightGray);
		setFocusable(true);
		// Adds keyboard press listeners for controlling the snake
		addKeyListener(new SnakeKeyListener());
		initGameBoard();
		// Creates and initiates the timer every DELAY interval
		// to generate 'ticks' that cause game action to occur.
		// Each tick fires 'actionPerformed' method to move game forward.
		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	// Called to 'draw' this Panel and everything in the game board
	// whenever the panel should be drawn or redrawn.
	// This is where all the actual painting of the 2D graphics occurs.
	// Need to paint the apple and the snake.
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Cast graphics to 2D for more modern API
		Graphics2D g2d = (Graphics2D) g;
		
		// Paint the state of the board
		// including the apple and the snake based on their coordinates
		if (isGameRunning) {
			// Paint apple as an oval
			g2d.setColor(APPLE_COLOR);
			g2d.fillOval(appleTile.x, appleTile.y, TILE_WIDTH, TILE_HEIGHT);
			
			// Paint snake as a series of squares based on tile coordinates
			Point currentSnakeTile;
			for (int ind = 0; ind < numSnakeTiles; ind++) {
				currentSnakeTile = snakeTiles[ind];
				// Paint the head with a special color...
				if (ind == 0) {
					// Paint the square
					g2d.setColor(SNAKE_HEAD_COLOR);
					g2d.fillRect(currentSnakeTile.x, currentSnakeTile.y,
							TILE_WIDTH, TILE_HEIGHT);
					// And paint a border
					g2d.setColor(SNAKE_HEAD_COLOR);
					g2d.drawRect(currentSnakeTile.x, currentSnakeTile.y,
							TILE_WIDTH, TILE_HEIGHT);
				} else {
					// Paint the square
					g2d.setColor(SNAKE_TAIL_COLOR);
					g2d.fillRect(currentSnakeTile.x, currentSnakeTile.y,
							TILE_WIDTH, TILE_HEIGHT);
					// And paint a border
					g2d.setColor(SNAKE_HEAD_COLOR);
					g2d.drawRect(currentSnakeTile.x, currentSnakeTile.y,
							TILE_WIDTH, TILE_HEIGHT);
				}
			}
			
			// Paint the score at the top left
			int score = ((numSnakeTiles * DELAY) - (INITIAL_SNAKE_TILES * DELAY));
			String msg = "Score: " + score;
			Font small = new Font("Helvetica Nueue", Font.BOLD, 16);
			g.setColor(Color.black);
			g.setFont(small);
			g.drawString(msg, WIDTH - 100, 30);

			// Tell the System to do the Drawing now,
			// otherwise it can take a few extra ms until
			// drawing is done which looks very jerky
			Toolkit.getDefaultToolkit().sync();
			
			// Cleanup the graphics instance after use
			g.dispose();
		} else {
			// If game is not running, then the game is over...
			String msg = "Game Over! (Press 'g' to restart)";
			Font small = new Font("Helvetica Nueue", Font.BOLD, 16);
			FontMetrics metr = this.getFontMetrics(small);

			g.setColor(Color.black);
			g.setFont(small);
			g.drawString(msg, (WIDTH - metr.stringWidth(msg)) / 2, HEIGHT / 2);
		}
	}
	
	// This fires for every 'tick' of the timer
	// which means every specified interval. When this fires,
	// we should move the game forward which means checking
	// conditions (apple hit or game over) and moving the snake.
	public void actionPerformed(ActionEvent e) {
		// If game is running, check conditions and move
		if (isGameRunning) {
			checkApple();
			checkBoundsCollision();
			moveSnake();
		}
		
		// This triggers the panel to be repainted after movement occurs
		// This called the 'paintComponent' method to draw the board
		repaint();
	}
	
	// Set initial game values, position initial snake and apple
	public void initGameBoard() {
		direction = "right";
		isGameRunning = true;		
		// Place the snake tiles on the board
		numSnakeTiles = INITIAL_SNAKE_TILES;
		Point currentSnakeTile;
		for (int ind = 0; ind <= numSnakeTiles; ind++) {
			currentSnakeTile = snakeTiles[ind] = new Point();
			currentSnakeTile.x = ((WIDTH / 2) - 100) - (ind * TILE_WIDTH);
			currentSnakeTile.y = (HEIGHT / 2);
		}
		// Place the apple tile on the board
		repositionApple();
	}
	
	// Moves the snake in the current direction
	public void moveSnake() {
		// First since a snake slides across the tiles,
		// move each tile so that every tile is now equal
		// to the previous tile (to move all tiles).
		for (int ind = numSnakeTiles; ind > 0; ind--) {
			snakeTiles[ind].x = snakeTiles[(ind - 1)].x;
			snakeTiles[ind].y = snakeTiles[(ind - 1)].y;
		}
		
		// Based on the current direction, we need to
		// move the head tile towards the next tile in that direction...
		if (direction == "left") {
			snakeTiles[0].x -= TILE_WIDTH;
		} else if (direction == "right") {
			snakeTiles[0].x += TILE_WIDTH;
		} else if (direction == "up") {
			snakeTiles[0].y -= TILE_HEIGHT;
		} else if (direction == "down") {
			snakeTiles[0].y += TILE_HEIGHT;
		}
	}

	// Check if the snake has eaten the apple
	// If the snake has, increase snake length and reposition apple
	public void checkApple() {
		// When snake head is directly on the apple tile,
		// apple is then consumed
		if ((snakeTiles[0].x == appleTile.x)
		     && (snakeTiles[0].y == appleTile.y)) {
			numSnakeTiles++;
			snakeTiles[numSnakeTiles] = new Point();
			repositionApple();
		}
	}
	
	// Check that the snake has not hit the bounds of the game board
	public void checkBoundsCollision() {
		for (int ind = numSnakeTiles; ind > 0; ind--) {
			// Check if the snake ran into itself
			if ((ind > INITIAL_SNAKE_TILES) && (snakeTiles[0].x == snakeTiles[ind].x)
					&& (snakeTiles[0].y == snakeTiles[ind].y)) {
				isGameRunning = false;
			}
		}
		
		// Check if snake ran into the boundaries of the game board
		// Game is over if the snake head are outside board bounds
		if (snakeTiles[0].x > WIDTH) {
			isGameRunning = false;
		} else if (snakeTiles[0].x < 0) {
			isGameRunning = false;
		} else if (snakeTiles[0].y < 0) {
			isGameRunning = false;
		} else if (snakeTiles[0].y > HEIGHT) {
			isGameRunning = false;
		}
	}
	
	// Move the apple to a new spot on the board
	// Generate a random position and set the new coordinates for apple
	public void repositionApple() {
		// Multiplier is just a number to create new position for next apple
		int multiplier = WIDTH / TILE_WIDTH;
		// Generate a random x coordinate for next apple
		int randomCoordinate = (int) (Math.random() * multiplier);
		appleTile.x = ((randomCoordinate * TILE_WIDTH));
		// Generate a random y coordinate for next apple
		randomCoordinate = (int) (Math.random() * multiplier);
		appleTile.y = ((randomCoordinate * TILE_HEIGHT));
	}
	
	// Defines a KeyAdapter for handling key presses
	// This is for changing the direction of the snake based on keyboard arrow-pad input.
	// This KeyAdapter is then added to the Panel
	private class SnakeKeyListener extends KeyAdapter {
		// Fires whenever a key on the keyboard is pressed
		// Handle arrow keys and the 'g' key for starting a new game.
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();

			if ((key == KeyEvent.VK_LEFT) && !(direction == "right")) {
				direction = "left";
			}
			else if ((key == KeyEvent.VK_RIGHT) && !(direction == "left")) {
				direction = "right";
			}
			else if ((key == KeyEvent.VK_UP) && !(direction == "down")) {
				direction = "up";
			}
			else if ((key == KeyEvent.VK_DOWN) && !(direction == "up")) {
				direction = "down";
			} 
			
			// Restart game if it's over
			if ((key == KeyEvent.VK_G) && isGameRunning == false) {
			  initGameBoard();
			}
		}
	}
}