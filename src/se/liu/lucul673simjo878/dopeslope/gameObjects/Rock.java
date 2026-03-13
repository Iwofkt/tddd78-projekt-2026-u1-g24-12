package se.liu.simjolucul.dopeslope.gameObjects;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Rock extends Obstacle {

    private final BufferedImage texture;

    public Rock(int x, int screenHeight, BufferedImage texture) {
        super(x, screenHeight, texture);
        this.texture = texture;
    }

    @Override
    public void drawObstacle(Graphics g) {
        // --- Use texture if provided ---
        if (texture != null) {
            g.drawImage(texture, position.x, position.y+height/5, width, height, null);
            return;
        }

        // --- Fallback: draw a simple rock shape ---
        Graphics2D g2d = (Graphics2D) g.create();

        // Coordinates for an irregular polygon
        int[] xPoints = {
                position.x,
                position.x + width / 4,
                position.x + width,
                position.x + width - width / 6,
                position.x + width / 3
        };

        int[] yPoints = {
                position.y + height / 3,
                position.y,
                position.y + height / 4,
                position.y + height,
                position.y + height
        };

        // Base fill
        g2d.setColor(new Color(120, 120, 120));
        g2d.fillPolygon(xPoints, yPoints, xPoints.length);

        // Dark outline
        g2d.setColor(new Color(80, 80, 80));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawPolygon(xPoints, yPoints, xPoints.length);

        // Add a crack for detail
        g2d.setColor(new Color(90, 90, 90));
        g2d.drawLine(
                position.x + width / 2,
                position.y + height / 3,
                position.x + width / 2 + width / 8,
                position.y + height / 2
        );

        g2d.dispose();
    }
}