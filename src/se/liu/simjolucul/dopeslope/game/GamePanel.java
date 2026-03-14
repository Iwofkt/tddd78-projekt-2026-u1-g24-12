package se.liu.simjolucul.dopeslope.game;

import se.liu.simjolucul.dopeslope.Main;
import se.liu.simjolucul.dopeslope.handlers.interaction.InputHandler;
import se.liu.simjolucul.dopeslope.highscore.Highscore;
import se.liu.simjolucul.dopeslope.highscore.HighscoreList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;

/**
 * The main game panel that handles the game loop and rendering.
 * <p>
 * This panel manages the game state, updates the game logic at regular intervals,
 * and handles highscore saving when the game ends. It contains the game world,
 * player, obstacles, and all visual components.
 * </p>
 */
public class GamePanel extends JPanel implements ActionListener {

    /** The target frames per second for the game loop */
    public static final int FPS = 40;

    private final Map<GameModeType, HighscoreList> highscoreLists;

    private final GameBase gameBase;

    private final GameComponent component;

    private final Timer timer;

    private boolean running = false;

    private boolean highscoreSaved = false;


    public GamePanel(Main main, int width, int height, Map<GameModeType, HighscoreList> highscoreLists) {
        this.highscoreLists = highscoreLists;

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        gameBase = new GameBase(width, height);

        component = new GameComponent(gameBase, main, highscoreLists);
        final InputHandler inputHandler = new InputHandler(component, gameBase);
        gameBase.setInputHandler(inputHandler);

        add(component, BorderLayout.CENTER);

        // Timer for game loop - necessary for continuous updates
        timer = new Timer(1000 / FPS, this);
    }

    public void startGame(GameModeType gameModeType) {
        if (!running) {
            running = true;
            gameBase.restart(gameModeType);
            timer.start();
            component.requestFocusInWindow();
        }
    }

    public void stopGame() {
        if (running) {
            running = false;
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gameBase.update();

        if (gameBase.isGameOver()) {
            if (!highscoreSaved) {
                saveHighscore();
                highscoreSaved = true;
            }
        }

        if (gameBase.pollNewGameFlag()) {
            highscoreSaved = false;
        }
    }

    private int calculateScore(GameModeType mode) {
        switch (mode) {
            case ENDLESS:
                return (int) gameBase.getPlayer().getDistanceTraveled() / 100;
            default:
                if (gameBase.hasFinishedRace()) {
                    return gameBase.getElapsedMilliseconds();
                } else {
                    return Highscore.getDnfScore();
                }
        }
    }

    private void saveHighscore() {
        boolean saved = false;
        String username = null;

        while (username == null || username.trim().isEmpty()) {
            username = JOptionPane.showInputDialog(
                    null,
                    "Vänligen skriv in ditt användarnamn:\n",
                    "Ange Användarnamn",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (username != null) {
                username = username.trim();
            }
        }

        GameModeType mode = gameBase.getGameModeType();
        HighscoreList highscoreList = highscoreLists.get(mode);

        if (highscoreList == null) {
            return;
        }

        int scoreValue = calculateScore(mode);

        while (!saved) {
            try {
                highscoreList.addScore(new Highscore(username, scoreValue));
                saved = true;

            } catch (IOException ex) {
                ex.printStackTrace();

                int result = JOptionPane.showOptionDialog(
                        null,
                        "Ett fel uppstod när highscore skulle sparas:\n" + ex.getMessage() +
                        "\nVill du försöka igen?",
                        "Fel vid sparning",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Ja", "Nej"},
                        "Nej"
                );

                if (result != JOptionPane.YES_OPTION) {
                    saved = true; // User chose not to retry
                }
            }
        }
    }
}