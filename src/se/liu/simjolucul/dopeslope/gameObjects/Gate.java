package se.liu.simjolucul.dopeslope.gameObjects;

import java.awt.*;
import java.awt.image.BufferedImage;

import static se.liu.simjolucul.dopeslope.ConfigLoader.isDebug;

public class Gate extends Obstacle {
    private static final int GATE_WIDTH = 140;   // gap between the two poles

    private Rectangle leftHitbox;
    private Rectangle rightHitbox;
    private final BufferedImage textureLeft;
    private final BufferedImage textureRight;

    public Gate(int x, int screenHeight,int screenWidth, BufferedImage textureLeft, BufferedImage textureRight) {
        super(x, screenHeight, textureLeft);
        this.textureLeft = textureLeft;
        this.textureRight = textureRight;

        // Initialise hitboxes using the actual pole height (from texture)
        this.hitbox = new Rectangle(x+textureLeft.getWidth(), screenHeight, GATE_WIDTH-textureLeft.getWidth(), height / 4);
        this.leftHitbox = new Rectangle(0, screenHeight, x-5, height / 4);
        this.rightHitbox = new Rectangle(x + GATE_WIDTH+5, screenHeight, screenWidth-GATE_WIDTH-x, height / 4);
    }

    public Rectangle getLeftHitbox() {
        return leftHitbox;
    }

    public Rectangle getRightHitbox() {
        return rightHitbox;
    }

    @Override
    public void drawObstacle(Graphics g) {
        int leftPoleX = position.x;
        int rightPoleX = position.x + GATE_WIDTH;
        int poleTopY = position.y;

        // Pole size = width and height from the texture (via super)
        int poleWidth = width;
        int poleHeight = height;

        if (isDebug()) {
            g.setColor(new Color(0, 0, 128, 128));

            g.fillRect(leftHitbox.x, leftHitbox.y, leftHitbox.width, leftHitbox.height);
            g.fillRect(rightHitbox.x, rightHitbox.y, rightHitbox.width, rightHitbox.height);
        }

        // If both textures exist, draw them
        if (textureLeft != null && textureRight != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.drawImage(textureLeft, leftPoleX, poleTopY, poleWidth, poleHeight, null);
            g2d.drawImage(textureRight, rightPoleX, poleTopY, poleWidth, poleHeight, null);
            g2d.dispose();
            return;
        }

        // --- Fallback: draw procedural poles using the same dimensions ---
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

        int shadowWidth = 10;
        int shadowHeight = 10;

        int shadowXLeft = position.x - shadowWidth / 3;
        int shadowXRight = position.x + GATE_WIDTH - shadowWidth / 3;
        int shadowY = position.y + height - shadowHeight / 2;   // use actual pole height

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillOval(shadowXLeft, shadowY, shadowWidth, shadowHeight);
        g2d.fillOval(shadowXRight, shadowY, shadowWidth, shadowHeight);

        g2d.dispose();
    }

    @Override
    public void update(double playerSpeed) {
        position.y -= (int) playerSpeed;

        // Update hitboxes – note: height comes from the texture
        hitbox.setLocation(position.x+textureLeft.getWidth(), position.y + height / 2);
        leftHitbox.setLocation(0, position.y + height / 2);
        rightHitbox.setLocation(position.x + GATE_WIDTH + textureRight.getWidth() + 5, position.y + height / 2);
    }
}