package se.liu.simjolucul.dopeslope.gameobjects;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tree extends Obstacle {

    private final BufferedImage treeTexture;

    public Tree(int x, int screenHeight, BufferedImage treeTexture) {
        super(
                x,
                screenHeight, treeTexture
        );
        this.treeTexture = treeTexture;
    }

    @Override
    public void drawObstacle(Graphics g) {

        // --- Use texture if it exists ---
        if (treeTexture != null) {
            g.drawImage(treeTexture, position.x, position.y + height / 4, width, height, null);
            return;
        }

        // --- Otherwise draw procedural
        Graphics2D g2d = (Graphics2D) g.create();

        int[] xPoints = {
                position.x - width / 2,
                position.x + width + width / 2,
                position.x + width / 2
        };
        int[] yPoints = {
                position.y + height,
                position.y + height,
                position.y
        };

        g2d.setColor(new Color(34, 139, 34));
        g2d.fillPolygon(xPoints, yPoints, 3);

        g2d.setColor(new Color(80, 220, 80, 180));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawLine(xPoints[0], yPoints[0], xPoints[2], yPoints[2]);
        g2d.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);

        g2d.setColor(new Color(20, 100, 20));
        g2d.drawPolygon(xPoints, yPoints, 3);

        int stumpWidth = width / 4;
        int stumpHeight =  height / 6;
        int stumpX = position.x + width / 2 - stumpWidth / 2;
        int stumpY = position.y + height;

        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRoundRect(stumpX, stumpY, stumpWidth, stumpHeight, 6, 6);

        g2d.setColor(new Color(111, 56, 6));
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawRoundRect(stumpX, stumpY, stumpWidth, stumpHeight, 6, 6);

        g2d.dispose();
    }
}