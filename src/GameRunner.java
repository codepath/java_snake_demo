import javax.swing.JFrame;

public class GameRunner {
	public static void main(String[] args) {
		// Create the JFrame which creates the game application window
		JFrame gameFrame = new JFrame();

		// Setup window properties for frame
		// i.e can't resize, close on exit, and title
		gameFrame.setResizable(false);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setTitle("CodePath Snake Project");

		// Setup game board with size, add SnakeGame panel
		// and set the window visible
		gameFrame.setSize(625, 645);
		gameFrame.setLocationRelativeTo(null);
		gameFrame.add(new SnakeGame());
		gameFrame.setVisible(true);
	}
}