package se.liu.simjolucul.dopeslope.gameObjects;

import se.liu.simjolucul.dopeslope.handlers.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Finishline extends Obstacle {
    private static final int POLE_HEIGHT = 60;
    private static final int POLE_WIDTH = 8;

    private static final int GATE_WIDTH = 350;

    private BufferedImage textureLeft;
    private BufferedImage textureRight;

    public Finishline(int x, int screenHeight, int width, int height, String texturePack) {
        super(x, screenHeight, width, height);
        this.hitbox = new Rectangle(0, screenHeight, 1000, POLE_HEIGHT / 4);
        textureLeft = ImageLoader.loadTexture(texturePack, "flagBlue");
        textureRight = ImageLoader.loadTexture(texturePack, "flagBlue");
    }

    @Override
    public void drawObstacle(Graphics g) {
        int leftPoleX = position.x;
        int rightPoleX = position.x + GATE_WIDTH;

        int poleTopY = position.y;

        // --- Use texture if it exists ---
        if (textureLeft != null || textureRight != null) {
            g.drawImage(textureLeft, leftPoleX, poleTopY, width, height, null);
            g.drawImage(textureRight, rightPoleX, poleTopY, width, height, null);
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

        int shadowWidth = 10;
        int shadowHeight = 10;

        int shadowXLeft = position.x - shadowWidth / 3;
        int shadowXRight = position.x + GATE_WIDTH - shadowWidth / 3;
        int shadowY = position.y + POLE_HEIGHT - shadowHeight / 2;

        // Semi-transparent black
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillOval(shadowXLeft, shadowY, shadowWidth, shadowHeight);
        g2d.fillOval(shadowXRight, shadowY, shadowWidth, shadowHeight);
    }

    @Override
    public void update(double playerSpeed) {
        position.y -= (int) playerSpeed;
        hitbox.setLocation(0, position.y+height/2);
    }
}
