package se.liu.simjolucul.dopeslope;

import se.liu.simjolucul.dopeslope.game.GameModeType;
import se.liu.simjolucul.dopeslope.game.GamePanel;
import se.liu.simjolucul.dopeslope.highscore.HighscoreList;
import se.liu.simjolucul.dopeslope.menu.MenuPanel;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class Main{
    private final JFrame frame;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final GamePanel gamePanel;
    private final MenuPanel menuPanel;

    private final Map<GameModeType, HighscoreList> highscoreLists = new EnumMap<>(GameModeType.class);

    public static final int VIRTUAL_WIDTH = 800;
    public static final int VIRTUAL_HEIGHT = 1000;

    public Main() {
        frame = new JFrame("DopeSlope");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        loadHighscores();

        // Create CardLayout and its container
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create panels
        gamePanel = new GamePanel(this, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, highscoreLists);
        menuPanel = new MenuPanel(this, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        // Add panels as cards
        cardPanel.add(menuPanel, "menu");
        cardPanel.add(gamePanel, "game");

        // Set the card panel as the content pane
        frame.setContentPane(cardPanel);

        // Show the menu initially
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

    public Map<GameModeType, HighscoreList> getHighscoreLists() {
        return highscoreLists;
    }

    private void loadHighscores() {
        highscoreLists.put(GameModeType.Endless,
                           loadSingleHighscoreList("highscores_endless.json", false));

        highscoreLists.put(GameModeType.CombeDeCaron,
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
        // Run this code on the Swing Event Dispatch Thread (EDT) as soon as possible.
        SwingUtilities.invokeLater(() -> new Main().show());
    }
}