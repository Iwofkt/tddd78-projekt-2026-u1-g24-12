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

    // ====== Constants ======

    /** Texture positioning constants */
    public static class TextureOffset {
        public static final int Y_OFFSET_DIVISOR = 4;
    }

    /** Canopy geometry constants */
    public static class CanopyGeometry {
        public static final int OVERHANG_FACTOR = 2;
        public static final int VERTEX_COUNT = 3;

        /** Index constants for polygon points */
        public static class VertexIndices {
            public static final int LEFT = 0;
            public static final int RIGHT = 1;
            public static final int TOP = 2;
        }
    }

    /** Canopy colors */
    public static class CanopyColors {
        public static final Color FILL = new Color(34, 139, 34);      // Forest green
        public static final Color HIGHLIGHT = new Color(80, 220, 80, 180); // Light green with transparency
        public static final Color OUTLINE = new Color(20, 100, 20);    // Dark green
    }

    /** Stump geometry constants */
    public static class StumpGeometry {
        public static final int WIDTH_DIVISOR = 4;
        public static final int HEIGHT_DIVISOR = 6;
        public static final int ARC_SIZE = 6;
    }

    /** Stump colors */
    public static class StumpColors {
        public static final Color FILL = new Color(139, 69, 19);      // Brown
        public static final Color OUTLINE = new Color(111, 56, 6);    // Dark brown
    }

    /** Stroke widths */
    public static class StrokeWidths {
        public static final float HIGHLIGHT = 2.0f;
        public static final float STUMP_OUTLINE = 2.0f;
    }

    private final BufferedImage treeTexture;

    public Tree(int x, int screenHeight, BufferedImage treeTexture) {
        super(x, screenHeight, treeTexture);
        this.treeTexture = treeTexture;
    }

    @Override
    public void drawObstacle(Graphics g) {

        // ------ Use texture if it exists ------
        if (treeTexture != null) {
            g.drawImage(treeTexture,
                        position.x,
                        position.y + height / TextureOffset.Y_OFFSET_DIVISOR,
                        width, height, null);
            return;
        }

        // ------ Otherwise draw procedural ------
        Graphics2D g2d = (Graphics2D) g.create();

        // ------ Canopy triangle ------
        // Calculate canopy corner positions
        int canopyLeft   = position.x - width / CanopyGeometry.OVERHANG_FACTOR;
        int canopyRight  = position.x + width + width / CanopyGeometry.OVERHANG_FACTOR;
        int canopyTop    = position.y;
        int canopyBottom = position.y + height;

        // Define triangle points
        int[] xPoints = {
                canopyLeft,
                canopyRight,
                position.x + width / 2
        };

        int[] yPoints = {
                canopyBottom,
                canopyBottom,
                canopyTop
        };

        // Draw filled canopy
        g2d.setColor(CanopyColors.FILL);
        g2d.fillPolygon(xPoints, yPoints, CanopyGeometry.VERTEX_COUNT);

        // Draw highlight lines (creating a 3D effect)
        g2d.setColor(CanopyColors.HIGHLIGHT);
        g2d.setStroke(new BasicStroke(StrokeWidths.HIGHLIGHT));

        // Draw from left bottom to top
        g2d.drawLine(xPoints[CanopyGeometry.VertexIndices.LEFT],
                     yPoints[CanopyGeometry.VertexIndices.LEFT],
                     xPoints[CanopyGeometry.VertexIndices.TOP],
                     yPoints[CanopyGeometry.VertexIndices.TOP]);

        // Draw from right bottom to top
        g2d.drawLine(xPoints[CanopyGeometry.VertexIndices.RIGHT],
                     yPoints[CanopyGeometry.VertexIndices.RIGHT],
                     xPoints[CanopyGeometry.VertexIndices.TOP],
                     yPoints[CanopyGeometry.VertexIndices.TOP]);

        // Draw canopy outline
        g2d.setColor(CanopyColors.OUTLINE);
        g2d.drawPolygon(xPoints, yPoints, CanopyGeometry.VERTEX_COUNT);

        // ------ Stump ------
        int stumpWidth  = width  / StumpGeometry.WIDTH_DIVISOR;
        int stumpHeight = height / StumpGeometry.HEIGHT_DIVISOR;
        int stumpX = position.x + width / 2 - stumpWidth / 2;
        int stumpY = position.y + height;

        // Draw filled stump
        g2d.setColor(StumpColors.FILL);
        g2d.fillRoundRect(stumpX, stumpY, stumpWidth, stumpHeight,
                          StumpGeometry.ARC_SIZE, StumpGeometry.ARC_SIZE);

        // Draw stump outline
        g2d.setColor(StumpColors.OUTLINE);
        g2d.setStroke(new BasicStroke(StrokeWidths.STUMP_OUTLINE));
        g2d.drawRoundRect(stumpX, stumpY, stumpWidth, stumpHeight,
                          StumpGeometry.ARC_SIZE, StumpGeometry.ARC_SIZE);

        g2d.dispose();
    }
}