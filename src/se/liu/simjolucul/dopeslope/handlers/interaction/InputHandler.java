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
 * of specific keys such as left, right, quit, and pause.
 */
public class InputHandler {

    private EnumMap<Direction, Boolean> keyPresses = new EnumMap<>(Direction.class);

    private boolean quitPressed = false;
    private boolean pausePressed = false;

    private final PauseAction pauseAction;

    public InputHandler(JComponent pane, GameBase gameBase) {
	this.pauseAction = new PauseAction(gameBase);

        // Make sure pane can receive key events
        pane.setFocusable(true);
        pane.requestFocusInWindow();

        // Attach key listener
        pane.addKeyListener(new MyKeyAdapter());
    }

    public boolean isKeyPressed(Direction direction) {
        return keyPresses.getOrDefault(direction, false);
    }

    public boolean isQuitPressed() {
        return quitPressed;
    }

    public boolean isPausePressed() {
        return pausePressed;
    }

    public void setLeftPressed(boolean leftPressed) {
        keyPresses.put(Direction.LEFT, leftPressed);
    }

    public void setRightPressed(boolean rightPressed) {
        keyPresses.put(Direction.RIGHT, rightPressed);
    }

    public void checkActions() {

        if (quitPressed) {
            System.exit(1);
        }
    }

    private class MyKeyAdapter extends KeyAdapter
    {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A, KeyEvent.VK_LEFT -> keyPresses.put(Direction.LEFT, true);
                case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> keyPresses.put(Direction.RIGHT, true);
                case KeyEvent.VK_P -> {
                    if (!pausePressed) {
                        pausePressed = true;
                        pauseAction.actionPerformed(null);
                    }
                }
                case KeyEvent.VK_Q -> quitPressed = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A, KeyEvent.VK_LEFT -> keyPresses.put(Direction.LEFT, false);
                case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> keyPresses.put(Direction.RIGHT, false);
                case KeyEvent.VK_P -> pausePressed = false;
                case KeyEvent.VK_Q -> quitPressed = false;
            }
        }
    }
}