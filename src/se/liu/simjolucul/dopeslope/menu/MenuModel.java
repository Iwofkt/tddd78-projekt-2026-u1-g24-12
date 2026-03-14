package se.liu.simjolucul.dopeslope.menu;

import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.effects.snowfx.SnowFall;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the model for the menu system, handling menu screens, items, and snow effects.
 */
public class MenuModel {

    private static final int SNOW_SPEED = 10;
    private final int width;
    private final int height;
    private int selectedIndex = 0;
    private int hoveredIndex = -1;
    private String title = null;
    private List<MenuItem> items = null;
    private final SnowFall snowFall;

    public MenuModel(int width, int height) {
        this.width = width;
        this.height = height;
        setScreen(MenuScreen.MAIN);
        snowFall = new SnowFall(1, width);
        snowFall.initializeSnowfall(width, height);
    }

    /**
     * Changes the current screen to the specified menu screen.
     *
     * @param screens The menu screen to switch to (MAIN, MODE_SELECT, etc.)
     */
    public void setScreen(MenuScreen screens) {
        switch (screens) {
            case MAIN:
                title = "Dope Slope";
                items = Arrays.asList(
                        new MenuItem("Play", "PLAY"),
                        new MenuItem("Exit", "EXIT")
                );
                break;
            case MODE_SELECT:
                title = "Select Game Mode";
                items = Arrays.asList(
                        new MenuItem("Endless", "MODE_ENDLESS"),
                        new MenuItem("Combe De Caron", "MODE_COMBE"),
                        new MenuItem("Back", "BACK")
                );
                break;
        }
    }

    /**
     * Updates the menu, including updating the snow effect.
     */
    public void update() {
        snowFall.update(SNOW_SPEED);
    }

    /**
     * Gets the list of snow particles for rendering.
     *
     * @return The list of snow particles.
     */
    public List<Particle> getSnow() {
        return snowFall.getParticles();
    }

    public String getTitle() { return title; }
    public List<MenuItem> getItems() { return items; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getSelectedIndex() { return selectedIndex; }
    public void setSelectedIndex(int index) { this.selectedIndex = index; }
    public int getHoveredIndex() { return hoveredIndex; }
    public void setHoveredIndex(int index) { this.hoveredIndex = index; }

    public void selectNext() {
        if (items != null && !items.isEmpty()) {
            selectedIndex = (selectedIndex + 1) % items.size();
        }
    }

    public void selectPrevious() {
        if (items != null && !items.isEmpty()) {
            selectedIndex = (selectedIndex - 1 + items.size()) % items.size();
        }
    }

    public static class MenuItem {
        private final String label;
        private final String command;

        public MenuItem(String label, String command) {
            this.label = label;
            this.command = command;
        }

        public String getCommand() {
            return command;
        }

        public String getLabel() {
            return label;
        }
    }
}