package netJam;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyListener extends KeyAdapter {
	@Override
	public void keyPressed(KeyEvent e) {
		Game game = NetJam.game;
		if (game != null) {
			game.keyPressed(e.getKeyCode());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Game game = NetJam.game;
		if (game != null) {
			game.keyReleased(e.getKeyCode());
		}
	}
}
