package se.liu.simjolucul.dopeslope.handlers.interaction;

import se.liu.simjolucul.dopeslope.game.GameBase;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler {

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean quitPressed = false;

    private final GameBase gameBase;

    public InputHandler(JComponent pane, GameBase gameBase) {
        this.gameBase = gameBase;

        // Make sure pane can receive key events
        pane.setFocusable(true);
        pane.requestFocusInWindow();

        // Attach key listener
        pane.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> leftPressed = true;
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> rightPressed = true;
                    case KeyEvent.VK_Q -> {
                        if (e.isControlDown()) quitPressed = true;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> leftPressed = false;
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> rightPressed = false;
                }
            }
        });
    }

    //-- Getters to check key states in game loop --//
    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isQuitPressed() {
        return quitPressed;
    }

    public void resetQuit() {
        quitPressed = false; // call this after handling quit
    }

    //-- Setters --//

    public void setLeftPressed(boolean leftPressed) {
        this.leftPressed = leftPressed;
    }

    public void setRightPressed(boolean rightPressed) {
        this.rightPressed = rightPressed;
    }
}