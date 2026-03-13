package se.liu.simjolucul.dopeslope.gameobjects;

import se.liu.simjolucul.dopeslope.handlers.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents the finish line in alpine game modes.
 * <p>
 * The hitbox is a wide rectangle at the base of the poles that triggers race completion
 * when the player passes through it. The hitbox is not limited to between the poles
 *
 * @see Obstacle
 * @see se.liu.simjolucul.dopeslope.slopes.CombeDeCaron
 */
public class FinishLine extends Obstacle {
    private final static int SHADOW_WIDTH = 10;
    private final static int SHADOW_HEIGHT = 10;
    private static final int POLE_HEIGHT = 60;
    private static final int POLE_WIDTH = 8;

    private static final int GATE_WIDTH = 350;

    private BufferedImage poleTexture;

    public FinishLine(int x, int screenHeight, int width, int height, BufferedImage poleTexture) {
        super(x, screenHeight, width, height);
        this.poleTexture = poleTexture;
        setHitbox(new Rectangle(0, screenHeight, 1000, POLE_HEIGHT / 4));
    }

    @Override
    public void drawObstacle(Graphics g) {
        int leftPoleX = (int) position.getX();
        int rightPoleX = (int) position.getX() + GATE_WIDTH;

        int poleTopY = position.y;

        //Use texture if it exists
        if (poleTexture != null) {
            g.drawImage(poleTexture, leftPoleX, poleTopY, width, height, null);
            g.drawImage(poleTexture, rightPoleX, poleTopY, width, height, null);
            return;
        }

        // --- Otherwise draw procedural
        Graphics2D g2d = (Graphics2D) g;

        // --- poles (left and right) ---
        g2d.setColor(new Color(0, 255, 0));
        g2d.fillRect(leftPoleX, poleTopY, POLE_WIDTH, POLE_HEIGHT);
        g2d.setColor(new Color(0, 255, 0));
        g2d.fillRect(rightPoleX, poleTopY, POLE_WIDTH, POLE_HEIGHT);
    }

    @Override
    public void drawShadow(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        int shadowXLeft = position.x - SHADOW_WIDTH / 3;
        int shadowXRight = position.x + GATE_WIDTH - SHADOW_WIDTH / 3;
        int shadowY = position.y + POLE_HEIGHT - SHADOW_HEIGHT / 2;

        // Semi-transparent black
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillOval(shadowXLeft, shadowY, SHADOW_WIDTH, SHADOW_HEIGHT);
        g2d.fillOval(shadowXRight, shadowY, SHADOW_WIDTH, SHADOW_HEIGHT);
    }

    @Override
    public void update(double playerSpeed) {
        position.y -= (int) playerSpeed;
        hitbox.setLocation(0, position.y+height/2);
    }
}
