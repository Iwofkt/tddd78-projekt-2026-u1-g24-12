package se.liu.simjolucul.dopeslope.menu;

import se.liu.simjolucul.dopeslope.effects.Particle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * * Renders the main menu with scaling, snow effect, and mouse/keyboard interaction.
 */
public class MenuComponent extends JComponent {

    // ====== Layout and appearance constants ======

    private static final Color BACKGROUND_COLOR = new Color(20, 20, 30);
    private static final String FONT_NAME = "Arial";

    // ------ Title constants ------
    private static final int TITLE_FONT_SIZE = 48;
    private static final int TITLE_Y = 150;

    // ------ Menu item constants ------
    private static final int ITEM_FONT_SIZE_NORMAL = 32;
    private static final int ITEM_FONT_SIZE_SELECTED = 36;

    /** Starting Y-coordinate for the first menu item */
    private static final int ITEM_START_Y = 300;

    /** Vertical spacing between menu items */
    private static final int ITEM_LINE_HEIGHT = 50;
    private static final Color ITEM_SELECTED_COLOR = new Color(255, 215, 0);
    private static final Color ITEM_NORMAL_COLOR = Color.LIGHT_GRAY;


    // ====== Instance fields ======

    private final MenuModel menuModel;
    private final List<Rectangle> itemBounds;
    private final List<ActionListener> actionListeners = new ArrayList<>();

    // ====== Constructor ======

    public MenuComponent(MenuModel menuModel) {
        this.menuModel = menuModel;
        this.itemBounds = new ArrayList<>();
        setFocusable(true);

        setupKeyBindings();
        setupMouseHandling();
    }

    // ====== Key binding setup ======
    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        im.put(KeyStroke.getKeyStroke("W"), "moveUp");      // alternative
        im.put(KeyStroke.getKeyStroke("S"), "moveDown");    // alternative
        im.put(KeyStroke.getKeyStroke("ENTER"), "select");

        am.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuModel.selectPrevious();
                menuModel.setHoveredIndex(menuModel.getSelectedIndex());
                repaint();
            }
        });

        am.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuModel.selectNext();
                menuModel.setHoveredIndex(menuModel.getSelectedIndex());
                repaint();
            }
        });

        am.put("select", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = menuModel.getSelectedIndex();
                if (idx >= 0 && idx < menuModel.getItems().size()) {
                    String command = menuModel.getItems().get(idx).command;
                    fireActionPerformed(command);
                }
            }
        });
    }

    // ====== Mouse handling setup ======

    /** Sets up mouse interaction for hovering and clicking menu items */
    private void setupMouseHandling() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point virtual = convertToVirtual(e.getPoint());
                int index = -1;
                if (virtual != null) {
                    for (int i = 0; i < itemBounds.size(); i++) {
                        if (itemBounds.get(i).contains(virtual)) {
                            index = i;
                            break;
                        }
                    }
                }
                menuModel.setHoveredIndex(index);
                menuModel.setSelectedIndex(index);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                menuModel.setHoveredIndex(-1);
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (menuModel.getHoveredIndex() != -1) {
                    String command = menuModel.getItems().get(menuModel.getHoveredIndex()).command;
                    fireActionPerformed(command);
                }
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    // ====== Public methods ======
    public void addActionListener(ActionListener l) {
        actionListeners.add(l);
    }

    // ====== Private helper methods ======

    private void fireActionPerformed(String command) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
        for (ActionListener l : actionListeners) {
            l.actionPerformed(e);
        }
    }

    private Point convertToVirtual(Point p) {
        int pw = getWidth();
        int ph = getHeight();
        int virtualWidth = menuModel.getWidth();
        int virtualHeight = menuModel.getHeight();

        double scaleX = (double) pw / virtualWidth;
        double scaleY = (double) ph / virtualHeight;
        double scale = Math.min(scaleX, scaleY);

        int offsetX = (int) ((pw - virtualWidth * scale) / 2);
        int offsetY = (int) ((ph - virtualHeight * scale) / 2);

        // Check if point is within the scaled virtual area
        if (p.x < offsetX || p.x > offsetX + virtualWidth * scale ||
            p.y < offsetY || p.y > offsetY + virtualHeight * scale) {
            return null;
        }

        int virtualX = (int) ((p.x - offsetX) / scale);
        int virtualY = (int) ((p.y - offsetY) / scale);
        return new Point(virtualX, virtualY);
    }

    // ====== Painting ======
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Cache frequently used values
        int virtualWidth = menuModel.getWidth();
        int virtualHeight = menuModel.getHeight();
        int pw = getWidth();
        int ph = getHeight();

        // Calculate scaling and centering
        double scaleX = (double) pw / virtualWidth;
        double scaleY = (double) ph / virtualHeight;
        double scale = Math.min(scaleX, scaleY);

        int offsetX = (int) ((pw - virtualWidth * scale) / 2);
        int offsetY = (int) ((ph - virtualHeight * scale) / 2);

        // Apply transformation
        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);

        // Draw background
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, virtualWidth, virtualHeight);

        // Draw snow particles
        for (Particle p : menuModel.getSnow()) {
            p.draw(g2d);
        }

        // Draw title
        g2d.setColor(Color.WHITE);
        Font titleFont = new Font(FONT_NAME, Font.BOLD, TITLE_FONT_SIZE);
        g2d.setFont(titleFont);
        FontMetrics titleFm = g2d.getFontMetrics(titleFont);
        String title = menuModel.getTitle();
        int titleX = (virtualWidth - titleFm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, TITLE_Y);

        Font normalFont = new Font(FONT_NAME, Font.PLAIN, ITEM_FONT_SIZE_NORMAL);
        Font selectedFont = new Font(FONT_NAME, Font.BOLD, ITEM_FONT_SIZE_SELECTED);

        int y = ITEM_START_Y;
        itemBounds.clear();

        List<MenuModel.MenuItem> items = menuModel.getItems();
        for (int i = 0; i < items.size(); i++) {
            MenuModel.MenuItem item = items.get(i);
            String text = item.label;

            // Determine font and color based on selection state
            boolean isSelected = (i == menuModel.getSelectedIndex());
            Font currentFont = isSelected ? selectedFont : normalFont;
            g2d.setFont(currentFont);
            g2d.setColor(isSelected ? ITEM_SELECTED_COLOR : ITEM_NORMAL_COLOR);

            FontMetrics fm = g2d.getFontMetrics(currentFont);

            // Calculate position and bounds
            int x = (virtualWidth - fm.stringWidth(text)) / 2;
            int textTopY = y - fm.getAscent();

            Rectangle rect = new Rectangle(x, textTopY, fm.stringWidth(text), fm.getHeight());
            itemBounds.add(rect);

            g2d.drawString(text, x, y);

            y += ITEM_LINE_HEIGHT;
        }

        g2d.dispose();
    }
}