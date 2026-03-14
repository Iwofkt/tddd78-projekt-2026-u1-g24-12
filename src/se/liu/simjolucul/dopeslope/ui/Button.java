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

    private Color normalColor = Color.lightGray;
    private Color hoverColor = new Color(150, 150, 150);
    private Color textColor = Color.DARK_GRAY;
    private Font font = new Font("Arial", Font.BOLD, 24);

    public Button(int x, int y, int width, int height, String text, Runnable onClick) {
        this.bounds = new Rectangle(x, y, width, height);
        this.text = text;
        this.onClick = onClick;
    }

    public void setHovered(Point p) {
        boolean now = p != null && bounds.contains(p);
        if (now != hovered) {
            hovered = now;
        }
    }

    public void handleClick(Point p) {
        if (p != null && bounds.contains(p)) {
            onClick.run();
        }
    }

    public void draw(Graphics2D g2d) {
        // Save original color/font if you want to restore later (optional)
        Color originalColor = g2d.getColor();
        Font originalFont = g2d.getFont();

        // Fill background
        g2d.setColor(hovered ? hoverColor : normalColor);
	final int cornerRadius = DEFAULT_CORNER_RADIUS;
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