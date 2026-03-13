package se.liu.simjolucul.dopeslope.gameobjects;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents a tree obstacle in the game.
 * <p>
 * The tree can be drawn either using a provided texture image or, if no texture is supplied,
 * a fallback hand-drawn polygon shape representing a triangular tree with a stump.
 * </p>
 */
public class Tree extends Obstacle {

    // Texture offset
    private static final int TEXTURE_Y_OFFSET_DIVISOR = 4;

    // Canopy geometry
    private static final int CANOPY_OVERHANG_FACTOR = 2;          // extends left/right by half width
    private static final int TRIANGLE_VERTEX_COUNT = 3;

    // Canopy colors
    private static final Color CANOPY_FILL_COLOR = new Color(34, 139, 34);
    private static final Color CANOPY_HIGHLIGHT_COLOR = new Color(80, 220, 80, 180);
    private static final Color CANOPY_OUTLINE_COLOR = new Color(20, 100, 20);

    // Stump geometry
    private static final int STUMP_WIDTH_DIVISOR = 4;
    private static final int STUMP_HEIGHT_DIVISOR = 6;
    private static final int STUMP_ARC_SIZE = 6;

    // Stump colors
    private static final Color STUMP_FILL_COLOR = new Color(139, 69, 19);
    private static final Color STUMP_OUTLINE_COLOR = new Color(111, 56, 6);

    // Stroke widths
    private static final float HIGHLIGHT_STROKE_WIDTH = 2.0f;
    private static final float STUMP_OUTLINE_STROKE_WIDTH = 2.0f;

    private final BufferedImage treeTexture;

    public Tree(int x, int screenHeight, BufferedImage treeTexture) {
        super(x, screenHeight, treeTexture);
        this.treeTexture = treeTexture;
    }

    @Override
    public void drawObstacle(Graphics g) {

        // --- Use texture if it exists ---
        if (treeTexture != null) {
            g.drawImage(treeTexture,
                        position.x,
                        position.y + height / TEXTURE_Y_OFFSET_DIVISOR,
                        width, height, null);
            return;
        }

        // --- Otherwise draw procedural
        Graphics2D g2d = (Graphics2D) g.create();

        // Canopy triangle
        int canopyLeft   = position.x - width / CANOPY_OVERHANG_FACTOR;
        int canopyRight  = position.x + width + width / CANOPY_OVERHANG_FACTOR;
        int canopyTop    = position.y;
        int canopyBottom = position.y + height;

        int[] xPoints = { canopyLeft, canopyRight, position.x + width / 2 };
        int[] yPoints = { canopyBottom, canopyBottom, canopyTop };

        g2d.setColor(CANOPY_FILL_COLOR);
        g2d.fillPolygon(xPoints, yPoints, TRIANGLE_VERTEX_COUNT);

        g2d.setColor(CANOPY_HIGHLIGHT_COLOR);
        g2d.setStroke(new BasicStroke(HIGHLIGHT_STROKE_WIDTH));
        g2d.drawLine(xPoints[0], yPoints[0], xPoints[2], yPoints[2]);
        g2d.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);

        g2d.setColor(CANOPY_OUTLINE_COLOR);
        g2d.drawPolygon(xPoints, yPoints, TRIANGLE_VERTEX_COUNT);

        // Stump
        int stumpWidth  = width  / STUMP_WIDTH_DIVISOR;
        int stumpHeight = height / STUMP_HEIGHT_DIVISOR;
        int stumpX = position.x + width / 2 - stumpWidth / 2;
        int stumpY = position.y + height;

        g2d.setColor(STUMP_FILL_COLOR);
        g2d.fillRoundRect(stumpX, stumpY, stumpWidth, stumpHeight,
                          STUMP_ARC_SIZE, STUMP_ARC_SIZE);

        g2d.setColor(STUMP_OUTLINE_COLOR);
        g2d.setStroke(new BasicStroke(STUMP_OUTLINE_STROKE_WIDTH));
        g2d.drawRoundRect(stumpX, stumpY, stumpWidth, stumpHeight,
                          STUMP_ARC_SIZE, STUMP_ARC_SIZE);

        g2d.dispose();
    }
}