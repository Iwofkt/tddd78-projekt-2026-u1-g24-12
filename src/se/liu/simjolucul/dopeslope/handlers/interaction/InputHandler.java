package se.liu.simjolucul.dopeslope.handlers.interaction;

import se.liu.simjolucul.dopeslope.game.Direction;
import se.liu.simjolucul.dopeslope.game.GameBase;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.EnumMap;

/**
 * Handles user input for controlling the game.
 * This class listens for key presses and releases, tracking the state
 * of specific keys such as left, right, and quit.
 */
public class InputHandler {

    private EnumMap<Direction, Boolean> keyPresses = new EnumMap<>(Direction.class);

    private boolean quitPressed = false;

    public InputHandler(JComponent pane, GameBase gameBase) {

	// Make sure pane can receive key events
        pane.setFocusable(true);
        pane.requestFocusInWindow();

        // Attach key listener
        pane.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> keyPresses.put(Direction.LEFT, true);
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> keyPresses.put(Direction.RIGHT, true);
                    case KeyEvent.VK_Q -> {
                        if (e.isControlDown()) quitPressed = true;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> keyPresses.put(Direction.LEFT, false);
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> keyPresses.put(Direction.RIGHT, false);
                }
            }
        });
    }

    public boolean isKeyPressed(Direction direction) {
        return keyPresses.getOrDefault(direction, false);
    }

    public boolean isQuitPressed() {
        return quitPressed;
    }

    public void resetQuit() {
        quitPressed = false;
    }

    public void setLeftPressed(boolean leftPressed) {
        keyPresses.put(Direction.LEFT, leftPressed);
    }

    public void setRightPressed(boolean rightPressed) {
        keyPresses.put(Direction.RIGHT, rightPressed);
    }
}