package se.liu.simjolucul.dopeslope.ui;

import java.awt.*;

/**
 * A simple clickable button that works in a virtual coordinate system.
 * Handles hover state and invokes a callback when clicked.
 */
public class Button {

    private static final int DEFAULT_CORNER_RADIUS = 10;
    private static final int TEXT_VERTICAL_OFFSET = 2;

    private final Rectangle bounds;
    private final String text;
    private final Runnable onClick;
    private boolean hovered = false;

    private Color normalColor = Color.GREEN;
    private Color hoverColor = new Color(100, 200, 100);
    private Color textColor = Color.BLACK;
    private Font font = new Font("Arial", Font.BOLD, 24);
    private int cornerRadius = DEFAULT_CORNER_RADIUS;

    public Button(int x, int y, int width, int height, String text, Runnable onClick) {
        this.bounds = new Rectangle(x, y, width, height);
        this.text = text;
        this.onClick = onClick;
    }

    // -- SETTER -- //
    public void setNormalColor(Color color) { this.normalColor = color; }
    public void setHoverColor(Color color) { this.hoverColor = color; }
    public void setTextColor(Color color) { this.textColor = color; }
    public void setFont(Font font) { this.font = font; }
    public void setCornerRadius(int radius) { this.cornerRadius = radius; }

    /**
     * Call this from your mouseMoved handler (with virtual coordinates).
     * @param p virtual mouse position (or null if outside content area)
     */
    public void setHovered(Point p) {
        boolean now = p != null && bounds.contains(p);
        if (now != hovered) {
            hovered = now;
        }
    }

    /**
     * Call this from your mouseClicked handler (with virtual coordinates).
     * If the click is inside, the action is executed.
     */
    public void handleClick(Point p) {
        if (p != null && bounds.contains(p)) {
            onClick.run();
        }
    }

    /**
     * Draw the button. The Graphics2D should already be scaled and translated
     * to virtual coordinates.
     */
    public void draw(Graphics2D g2d) {
        // Save original color/font if you want to restore later (optional)
        Color originalColor = g2d.getColor();
        Font originalFont = g2d.getFont();

        // Fill background
        g2d.setColor(hovered ? hoverColor : normalColor);
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, cornerRadius, cornerRadius);

        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, cornerRadius, cornerRadius);

        // Draw text centered
        g2d.setColor(textColor);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + (bounds.height + fm.getAscent()) / 2 - TEXT_VERTICAL_OFFSET;
        g2d.drawString(text, textX, textY);

        // Restore (optional)
        g2d.setColor(originalColor);
        g2d.setFont(originalFont);
    }
}