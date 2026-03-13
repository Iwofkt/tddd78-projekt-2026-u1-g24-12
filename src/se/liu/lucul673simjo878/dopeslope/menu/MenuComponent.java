package se.liu.simjolucul.dopeslope.menu;

import se.liu.simjolucul.dopeslope.effects.snowfx.SnowParticle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MenuComponent extends JComponent {
    private MenuModel menuModel;
    private List<Rectangle> itemBounds;
    private List<ActionListener> actionListeners = new ArrayList<>();

    public MenuComponent(MenuModel menuModel) {
        this.menuModel = menuModel;
        this.itemBounds = new ArrayList<>();
        setFocusable(true);
        InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        im.put(KeyStroke.getKeyStroke("W"), "moveUp");      // optional alternative
        im.put(KeyStroke.getKeyStroke("S"), "moveDown");    // optional alternative
        im.put(KeyStroke.getKeyStroke("ENTER"), "select");

        am.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuModel.selectPrevious();
                menuModel.setHoveredIndex(menuModel.getSelectedIndex());
                repaint();  // because model doesn't notify on selection change
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

        // Mouse handling (similar to before, but using model's items)
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

    public void addActionListener(ActionListener l) {
        actionListeners.add(l);
    }

    private void fireActionPerformed(String command) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
        for (ActionListener l : actionListeners) {
            l.actionPerformed(e);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Scaling and centering
        int pw = getWidth();
        int ph = getHeight();
        double scaleX = (double) pw / menuModel.getWidth();
        double scaleY = (double) ph / menuModel.getHeight();
        double scale = Math.min(scaleX, scaleY);
        int offsetX = (int) ((pw - menuModel.getWidth() * scale) / 2);
        int offsetY = (int) ((ph - menuModel.getHeight() * scale) / 2);
        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);

        // Background
        g2d.setColor(new Color(20, 20, 30));
        g2d.fillRect(0, 0, menuModel.getWidth(), menuModel.getHeight());


        for(SnowParticle p : menuModel.getSnow()){
            p.draw(g2d);
        }

        // Draw title
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        String title = menuModel.getTitle();
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (menuModel.getWidth() - fm.stringWidth(title)) / 2;
        int titleY = 150;
        g2d.drawString(title, titleX, titleY);

        // Draw menu items
        g2d.setFont(new Font("Arial", Font.PLAIN, 32));
        fm = g2d.getFontMetrics();
        int y = 300;
        int lineHeight = 50;
        itemBounds.clear();

        List<MenuModel.MenuItem> items = menuModel.getItems();
        for (int i = 0; i < items.size(); i++) {
            MenuModel.MenuItem item = items.get(i);
            String text = item.label;
            int x = (menuModel.getWidth() - fm.stringWidth(text)) / 2;
            int textTopY = y - fm.getAscent();
            Rectangle rect = new Rectangle(x, textTopY, fm.stringWidth(text), fm.getHeight());
            itemBounds.add(rect);

            boolean isSelected = (i == menuModel.getSelectedIndex());
            boolean isHovered = (i == menuModel.getHoveredIndex());

            if (isSelected) {
                g2d.setColor(new Color(255, 215, 0));
                g2d.setFont(new Font("Arial", Font.BOLD, 36));
            } else {
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 32));
            }

            fm = g2d.getFontMetrics();
            x = (menuModel.getWidth() - fm.stringWidth(text)) / 2;
            g2d.drawString(text, x, y);
            y += lineHeight;
        }
        g2d.dispose();
    }

    private Point convertToVirtual(Point screenPoint) {
        int pw = getWidth();
        int ph = getHeight();
        int vw = menuModel.getWidth();
        int vh = menuModel.getHeight();

        double scaleX = (double) pw / vw;
        double scaleY = (double) ph / vh;
        double scale = Math.min(scaleX, scaleY);
        int offsetX = (int) ((pw - vw * scale) / 2);
        int offsetY = (int) ((ph - vh * scale) / 2);

        int sx = screenPoint.x;
        int sy = screenPoint.y;

        // Check if inside the scaled content area
        if (sx < offsetX || sx >= offsetX + vw * scale ||
                sy < offsetY || sy >= offsetY + vh * scale) {
            return null; // outside the scaled drawing area
        }

        int vx = (int) ((sx - offsetX) / scale);
        int vy = (int) ((sy - offsetY) / scale);
        return new Point(vx, vy);
    }
}