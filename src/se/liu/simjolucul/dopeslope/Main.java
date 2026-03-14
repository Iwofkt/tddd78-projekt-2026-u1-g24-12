package se.liu.simjolucul.dopeslope;

import se.liu.simjolucul.dopeslope.game.GameModeType;
import se.liu.simjolucul.dopeslope.game.GamePanel;
import se.liu.simjolucul.dopeslope.highscore.HighscoreList;
import se.liu.simjolucul.dopeslope.menu.MenuPanel;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * The main application class for DopeSlope.
 * <p>
 * This class sets up the main window, manages the transition between
 * the menu and game panels, and loads highscore lists.
 */
public class Main {
    // Non-final fields because they are set in initialize()
    private JFrame frame = null;
    private CardLayout cardLayout = null;
    private JPanel cardPanel = null;
    private GamePanel gamePanel = null;
    private MenuPanel menuPanel = null;

    private final Map<GameModeType, HighscoreList> highscoreLists;

    /** The virtual width of the game window in pixels. */
    public static final int VIRTUAL_WIDTH = 800;

    /** The virtual height of the game window in pixels. */
    public static final int VIRTUAL_HEIGHT = 1000;

    public Main() {
        highscoreLists = new EnumMap<>(GameModeType.class);
    }

    public void initialize() {
        loadHighscores();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("DopeSlope");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        gamePanel = new GamePanel(this, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, highscoreLists);
        menuPanel = new MenuPanel(this, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        cardPanel.add(menuPanel, "menu");
        cardPanel.add(gamePanel, "game");

        frame.setContentPane(cardPanel);
        showMenu();
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public void showMenu() {
        menuPanel.startMenu();
        cardLayout.show(cardPanel, "menu");
        gamePanel.stopGame();
    }

    public void startGame(GameModeType gameModeType) {
        menuPanel.stopMenu();
        cardLayout.show(cardPanel, "game");
        gamePanel.startGame(gameModeType);
    }

    private void loadHighscores() {
        highscoreLists.put(GameModeType.ENDLESS,
                           loadSingleHighscoreList("highscores_endless.json", false));
        highscoreLists.put(GameModeType.COMBE_DE_CARON,
                           loadSingleHighscoreList("highscores_combedecaron.json", true));
    }

    private static HighscoreList loadSingleHighscoreList(String filename, boolean lowerIsBetter) {
        boolean loaded = false;
        HighscoreList highscoreList = new HighscoreList(filename, lowerIsBetter);

        while (!loaded) {
            try {
                highscoreList = HighscoreList.load(filename, lowerIsBetter);
                loaded = true;
            } catch (RuntimeException ex) {
                int result = JOptionPane.showOptionDialog(
                        null,
                        """
                        The highscore file could not be loaded.
                        It may not exist or an error occurred while reading it.

                        Error message:
                        """ + ex.getMessage() + """
                        Do you want to try loading the file again?
                        """,
                        "Highscore Error",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null,
                        new Object[]{"Retry", "Create New"},
                        "Create New"
                );

                if (result == JOptionPane.NO_OPTION) {
                    loaded = true;
                    highscoreList = new HighscoreList(filename, lowerIsBetter);
                }
            }
        }
        return highscoreList;
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.initialize();
            main.show();
        });
    }
}