package codepath.snake;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
public class GameEngine extends JPanel implements ActionListener {
	// Sets the width and height of the canvas to 300x300
	private final int WIDTH = 600;
	private final int HEIGHT = 600;

	// Defines the width of the actors (objects) painted on the board
	private final int TILE_WIDTH = 25;
	private final int TILE_HEIGHT = 25;

	// Define the delay between 'ticks' of the timer
	// Timer will trigger actionPerformed method every 100ms
	private int currentDelay = 150;
	private int currentScore = 0;
	
	// Keeps track of if the game is currently running
	// Game stops when a game over condition is reached (i.e snake hits the wall)
	private boolean isGameRunning;
	private boolean isGamePaused;
	
	// Defines snake object for game board
	private Snake snake;
	
	// Defines apple object for game board
	private Apple apple;
	
	// Creates a timer which 'ticks' and will trigger
	// the actionPerformed method every X interval
	// This is how the game progresses, the snake moves in 
	// the specified direction every 'tick' of the timer.
	private Timer timer;

	// Constructor for the SnakeGame Panel
	// Creates game board and starts game timer
	public GameEngine() {
		setBackground(Color.lightGray);
		setFocusable(true);
		// Adds keyboard press listeners for controlling the snake
		addKeyListener(new SnakeKeyListener());
		initGameBoard();
		// Creates and initiates the timer every DELAY interval
		// to generate 'ticks' that cause game action to occur.
		// Each tick fires 'actionPerformed' method to move game forward.
		timer = new Timer(currentDelay, this);
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
			// Paint apple
			apple.paintActor(g2d);
			// Paint snake
			snake.paintActor(g2d);
			// Draw score
			drawScore(g);
			
			// Draw paused if game is paused
		    if (isGamePaused) {
			  drawCenterText("Paused! (Press 'p' to unpause)", g);
			}

			// Tell the System to do the Drawing now,
			// otherwise it can take a few extra ms until
			// drawing is done which looks very jerky
			Toolkit.getDefaultToolkit().sync();
			
			// Cleanup the graphics instance after use
			g.dispose();
		} else {
			// If game is not running, then the game is over...
			drawCenterText("Game Over! (Press 'g' to restart)", g);
		}  
	}

	// Paint the game over text
	private void drawCenterText(String msg, Graphics g) {
		Font small = new Font("Helvetica Nueue", Font.BOLD, 16);
		FontMetrics metr = this.getFontMetrics(small);

		g.setColor(Color.black);
		g.setFont(small);
		g.drawString(msg, (WIDTH - metr.stringWidth(msg)) / 2, HEIGHT / 2);
	}

	// Paint the score at the top left
	private void drawScore(Graphics g) {
		String msg = "Score: " + currentScore;
		Font small = new Font("Helvetica Nueue", Font.BOLD, 16);
		g.setColor(Color.black);
		g.setFont(small);
		g.drawString(msg, WIDTH - 100, 30);
	}
	
	// This fires for every 'tick' of the timer
	// which means every specified interval. When this fires,
	// we should move the game forward which means checking
	// conditions (apple hit or game over) and moving the snake.
	public void actionPerformed(ActionEvent e) {
		// If game is running, check conditions and move
		if (isGameRunning && !isGamePaused) {
			checkApple();
			checkBoundsCollision();
			snake.move();			
		}
		
		// This triggers the panel to be repainted after movement occurs
		// This called the 'paintComponent' method to draw the board
		repaint();
	}
	
	// Set initial game values, position initial snake and apple
	public void initGameBoard() {
		// Initialize snake and apple
		snake = new Snake(TILE_WIDTH, TILE_HEIGHT);
		apple = new Apple(TILE_WIDTH, TILE_HEIGHT);
		// Place the snake on the board
		snake.setStartPos((WIDTH / 2) - 100, (HEIGHT / 2));
		// Place the apple tile on the board
		apple.reposition(WIDTH / TILE_WIDTH);
		// Mark game as running
		currentDelay = 150;
		currentScore = 0;
		isGameRunning = true;	
		isGamePaused = false;
		if (timer != null) { timer.setDelay(currentDelay); }
	}

	// Check if the snake has eaten the apple
	// If the snake has, increase snake length and reposition apple
	public void checkApple() {
		if (snake.hasHitApple(apple)) {
			// Move apple and grow snake tail by one
			snake.growTail();
			apple.reposition(WIDTH / TILE_WIDTH);
			// Adjust score based on current delay
			currentScore += currentDelay;
			// Increase delay once every 10 apples
			if (snake.getNumTiles() % 10 == 0 && currentDelay > 75) { 
				timer.setDelay(currentDelay -= 25); 
		    } 
		}
	}
	
	// Check that the snake has not hit the bounds of the game board
	public void checkBoundsCollision() {
		// Check if the snake ran into itself
		if (snake.hasHitSelf()) {
		  isGameRunning = false;
		}
		// Check if the snake hit the bounds
		if (snake.hasHitBounds(WIDTH, HEIGHT)) {
			isGameRunning = false;
		}
	}
	
	// Defines a KeyAdapter for handling key presses
	// This is for changing the direction of the snake based on keyboard arrow-pad input.
	// This KeyAdapter is then added to the Panel
	private class SnakeKeyListener extends KeyAdapter {
		// Fires whenever a key on the keyboard is pressed
		// Handle arrow keys and the 'g' key for starting a new game.
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();

			if ((key == KeyEvent.VK_LEFT) && !(snake.getDirection() == "right")) {
				snake.setDirection("left");
			}
			else if ((key == KeyEvent.VK_RIGHT) && !(snake.getDirection() == "left")) {
				snake.setDirection("right");
			}
			else if ((key == KeyEvent.VK_UP) && !(snake.getDirection() == "down")) {
				snake.setDirection("up");
			}
			else if ((key == KeyEvent.VK_DOWN) && !(snake.getDirection() == "up")) {
				snake.setDirection("down");
			} 
			
			// Restart game if it's over
			if ((key == KeyEvent.VK_G) && isGameRunning == false) {
			  initGameBoard();
			}
			
			// Support pausing and unpausing
			if ((key == KeyEvent.VK_P) && isGameRunning == true) {
		      isGamePaused = !isGamePaused;
			}
		}
	}
}