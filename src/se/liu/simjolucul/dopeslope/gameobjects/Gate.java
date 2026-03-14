package se.liu.simjolucul.dopeslope.gameobjects;

import java.awt.*;
import java.awt.image.BufferedImage;

import static se.liu.simjolucul.dopeslope.ConfigLoader.isDebug;

/**
 * Represents a gate obstacle in the game, consisting of two vertical poles with a gap between them.
 * <p>
 * The gate uses three hitboxes:
 * <ul>
 *   <li>The main hitbox (inherited from {@link Obstacle}) covers the gap area – passing through it
 *       is typically considered a successful gate pass (scoring).</li>
 *   <li>{@link #leftHitbox} and {@link #rightHitbox} cover the areas outside the poles; collision
 *       with these indicates hitting the gate, which usually ends the game.</li>
 * </ul>
 * @see Obstacle
 * @see se.liu.simjolucul.dopeslope.handlers.collision.ObjectCollision
 */
public class Gate extends Obstacle {
    /** Width of the gap between the two poles */
    private static final int GATE_WIDTH = 140;

    /** Extra margin around hitboxes (in pixels) */
    private static final int HITBOX_MARGIN = 5;

    /** Hitbox height as a fraction of the pole height */
    private static final double HITBOX_HEIGHT_RATIO = 0.25;  // 1/4

    /** Vertical position of the hitbox as a fraction of pole height (from top) */
    private static final double HITBOX_VERTICAL_RATIO = 0.5; // 1/2

    // ------ Shadow constants ------

    private static final int SHADOW_SIZE = 10;
    private static final double SHADOW_OFFSET_X_RATIO = 0.333333;
    private static final double SHADOW_OFFSET_Y_RATIO = 0.5;
    private static final int SHADOW_ALPHA = 100;

    private Rectangle leftHitbox;
    private Rectangle rightHitbox;
    private final BufferedImage textureLeft;
    private final BufferedImage textureRight;

    public Gate(int x, int screenHeight, int screenWidth,
                BufferedImage textureLeft, BufferedImage textureRight) {
        super(x, screenHeight, textureLeft);
        this.textureLeft = textureLeft;
        this.textureRight = textureRight;

        int poleWidth = textureLeft.getWidth();
        int hitboxHeight = (int) (height * HITBOX_HEIGHT_RATIO);

        // Main hitbox (the gap area between poles)
        setHitbox(new Rectangle(x + poleWidth, screenHeight, GATE_WIDTH - poleWidth, hitboxHeight));

        // Left side hitbox (from left edge to just before left pole)
        this.leftHitbox = new Rectangle(
                0,
                screenHeight,
                x - HITBOX_MARGIN,
                hitboxHeight
        );

        // Right side hitbox (from after right pole to screen edge)
        this.rightHitbox = new Rectangle(
                x + GATE_WIDTH + HITBOX_MARGIN,
                screenHeight,
                screenWidth - GATE_WIDTH - x,
                hitboxHeight
        );
    }

    public Rectangle getLeftHitbox() {
        return leftHitbox;
    }

    public Rectangle getRightHitbox() {
        return rightHitbox;
    }

    @Override
    public void drawObstacle(Graphics g) {
        int leftPoleX = position.getLocation().x;
        int rightPoleX = position.getLocation().x + GATE_WIDTH;
        int poleTopY = position.getLocation().y;
        int poleWidth = width;
        int poleHeight = height;

        if (isDebug()) {
            g.setColor(new Color(0, 0, 128, 128)); // semi‑transparent blue
            g.fillRect(leftHitbox.x,leftHitbox.x, leftHitbox.width, leftHitbox.height);
            g.fillRect(rightHitbox.x, rightHitbox.x, rightHitbox.width, rightHitbox.height);
        }

        // Draw textures if available, otherwise fallback to colored rectangles
        if (textureLeft != null && textureRight != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.drawImage(textureLeft, leftPoleX, poleTopY, poleWidth, poleHeight, null);
            g2d.drawImage(textureRight, rightPoleX, poleTopY, poleWidth, poleHeight, null);
            g2d.dispose();
            return;
        }

        // Fallback procedural drawing
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(255, 0, 0));
        g2d.fillRect(leftPoleX, poleTopY, poleWidth, poleHeight);
        g2d.setColor(new Color(0, 0, 255));
        g2d.fillRect(rightPoleX, poleTopY, poleWidth, poleHeight);
        g2d.dispose();
    }

    @Override
    public void drawShadow(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        int shadowWidth = SHADOW_SIZE;
        int shadowHeight = SHADOW_SIZE;

        int shadowXLeft = (int) (position.getX() - shadowWidth * SHADOW_OFFSET_X_RATIO);
        int shadowXRight = (int) (position.getX() + GATE_WIDTH - shadowWidth * SHADOW_OFFSET_X_RATIO);
        int shadowY = position.y + height - (int) (shadowHeight * SHADOW_OFFSET_Y_RATIO);

        g2d.setColor(new Color(0, 0, 0, SHADOW_ALPHA));
        g2d.fillOval(shadowXLeft, shadowY, shadowWidth, shadowHeight);
        g2d.fillOval(shadowXRight, shadowY, shadowWidth, shadowHeight);

        g2d.dispose();
    }

    @Override
    public void update(double playerSpeed) {
        position.y -= (int) playerSpeed;

        int poleWidth = textureLeft.getWidth();
        int hitboxY = position.y + (int) (height * HITBOX_VERTICAL_RATIO);

        hitbox.setLocation(position.x + poleWidth, hitboxY);
        leftHitbox.setLocation(0, hitboxY);
        rightHitbox.setLocation(position.x + GATE_WIDTH + poleWidth + HITBOX_MARGIN, hitboxY);
    }
}