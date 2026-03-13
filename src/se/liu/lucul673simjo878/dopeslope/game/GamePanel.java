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

public class GamePanel extends JPanel implements ActionListener {
    private final Main main;
    private final int width;
    private final int height;
    private final Map<GameModeType, HighscoreList> highscoreLists;

    private final GameBase gameBase;
    private final GameComponent component;
    private final InputHandler inputHandler;
    private final Timer timer;

    private boolean running = false;
    private boolean highscoreSaved = false;

    public static final int FPS = 40;

    public GamePanel(Main main, int width, int height, Map<GameModeType, HighscoreList> highscoreLists) {
        this.main = main;
        this.width = width;
        this.height = height;
        this.highscoreLists = highscoreLists;

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        gameBase = new GameBase(width, height);

        component = new GameComponent(gameBase, main, highscoreLists);
        inputHandler = new InputHandler(component, gameBase);
        gameBase.setInputHandler(inputHandler);

        add(component, BorderLayout.CENTER);

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

        if (gameBase.getGameOver()) {
            if (!highscoreSaved) {
                saveHighscore();
                highscoreSaved = true;
            }
        }

        if (gameBase.newGame()) {
            highscoreSaved = false;
        }
    }

    private void saveHighscore() {
        boolean saved = false;
        String username = null;

        while (username == null) {
            username = JOptionPane.showInputDialog(
                    null,
                    "Vänligen skriv in ditt användarnamn:\n",
                    "Ange Användarnamn",
                    JOptionPane.QUESTION_MESSAGE
            );
        }

        username = username.trim();

        GameModeType mode = gameBase.getGameModeType();
        HighscoreList highscoreList = highscoreLists.get(mode);

        if (highscoreList == null) {
            return;
        }

        int scoreValue;

        switch (mode) {
            case Endless:
                scoreValue = (int) gameBase.getPlayer().getDistanceTraveled() / 100;
                break;
            case CombeDeCaron:
                if (gameBase.hasFinishedRace()) {
                    scoreValue = gameBase.getElapsedCentiseconds();
                } else {
                    scoreValue = Highscore.DNF_SCORE;
                }
                break;
            default:
                scoreValue = (int) gameBase.getPlayer().getDistanceTraveled() / 100;
                break;
        }

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
                    saved = true;
                }
            }
        }
    }
}