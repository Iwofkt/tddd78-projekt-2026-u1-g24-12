package se.liu.simjolucul.dopeslope.game;

import se.liu.simjolucul.dopeslope.Main;
import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.gameObjects.Finishline;
import se.liu.simjolucul.dopeslope.gameObjects.Gate;
import se.liu.simjolucul.dopeslope.gameObjects.Obstacle;
import se.liu.simjolucul.dopeslope.gameObjects.Player;
import se.liu.simjolucul.dopeslope.highscore.Highscore;
import se.liu.simjolucul.dopeslope.highscore.HighscoreList;
import se.liu.simjolucul.dopeslope.ui.Button;

import java.util.Map;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameComponent extends JComponent implements GameObserver
{

    private static final int TEXT_MARGIN = 10;
    private static final int TEXT_SIZE = 20;
    private static final float MENU_TRANSPARENCY = 0.4F;

    private final Map<GameModeType, HighscoreList> highscoreLists;
    private final GameBase gameBase;

    private final List<Button> pauseMenuButtons = new ArrayList<>();

    public GameComponent(GameBase gameBase, Main main, Map<GameModeType, HighscoreList> highscoreLists) {

        this.gameBase = gameBase;
        this.highscoreLists = highscoreLists;

        gameBase.addObserver(this);

        pauseMenuButtons.add(new Button(
                (gameBase.getWidth() - 200) / 2,
                gameBase.getHeight() * 2 / 3,
                200, 50,
                "RESTART",
                () -> gameBase.restart(gameBase.getGameModeType())
        ));

        pauseMenuButtons.add(new Button(
                (gameBase.getWidth() - 200) / 2,
                gameBase.getHeight() * 2 / 3 + 60,
                200, 50,
                "MAIN MENU",
                main::showMenu
        ));

        MouseAdapter mouseHandler = new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Point virtual = convertToVirtual(e.getPoint());
                for (Button btn : pauseMenuButtons) {
                    btn.setHovered(virtual);
                }
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse clicked at " + e.getPoint());
                Point virtual = convertToVirtual(e.getPoint());
                for (Button btn : pauseMenuButtons) {
                    btn.handleClick(virtual);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                for (Button btn : pauseMenuButtons) {
                    btn.setHovered(null);
                }
                repaint();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(gameBase.getWidth(), gameBase.getHeight());
    }

    @Override
    public void worldUpdated() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int vw = gameBase.getWidth();
        int vh = gameBase.getHeight();

        double scaleX = (double) panelWidth / vw;
        double scaleY = (double) panelHeight / vh;
        double scale = Math.min(scaleX, scaleY);

        int offsetX = (int) ((panelWidth - vw * scale) / 2);
        int offsetY = (int) ((panelHeight - vh * scale) / 2);

        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);

        Player player = gameBase.getPlayer();

        overlay(g2d, 1, Color.LIGHT_GRAY, vw, vh);

        for (Particle p : gameBase.getTrackParticles()) {
            p.draw(g2d);
        }

        player.draw(g2d);

        for (Obstacle obstacle : gameBase.getObstacles()) {
            obstacle.drawShadow(g2d);
        }

        for (Obstacle obstacle : gameBase.getObstacles()) {
            obstacle.draw(g2d, player.getCurrentSpeed());
        }

        for (Gate gate : gameBase.getGates()) {
            gate.drawShadow(g2d);
        }

        for (Gate gate : gameBase.getGates()) {
            gate.draw(g2d, player.getCurrentSpeed());
        }

        for (Finishline finishline : gameBase.getFinishline()) {
            finishline.drawShadow(g2d);
        }

        for (Finishline finishline : gameBase.getFinishline()) {
            finishline.draw(g2d, player.getCurrentSpeed());
        }

        for (Particle p : gameBase.getSprayParticles()) {
            p.draw(g2d);
        }

        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(35, 105, 158, 20),
                0, vh - 50, new Color(0, 0, 0, 50)
        );

        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, vw, vh);

        for (Particle p : gameBase.getSnowParticles()) {
            p.draw(g2d);
        }

        drawTextStat(g2d, "SPEED:", 1);
        drawStatBar(g2d, player.getYSpeed(), player.getMaxSpeed(), 4);

        drawTextStat(g2d, "DISTANCE:", 6);
        drawTextStat(g2d, (int) (player.getDistanceTraveled() / 100) + " m", 8);

        drawTextStat(g2d, "TIME:", 11);
        drawTextStat(g2d, gameBase.getFormattedGameTime(), 13);

        if (gameBase.getGameOver() || gameBase.getGamePaused()) {

            overlay(g2d, MENU_TRANSPARENCY,
                    gameBase.getGameOver() ? new Color(70, 0, 0) : new Color(0, 0, 70),
                    vw, vh);

            drawTitle(g2d,
                    gameBase.getGameOver() ? "GAME OVER" : "GAME PAUSED",
                    gameBase.getGameOver() ? Color.RED : Color.BLUE,
                    vw, vh);

            drawGameStats(g2d);

            for (Button btn : pauseMenuButtons) {
                btn.draw(g2d);
            }
        }

        g2d.dispose();
    }

    private void overlay(Graphics2D g2d, float transparency, Color color, int vw, int vh) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
        g2d.setColor(color);
        g2d.fillRect(0, 0, vw, vh);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }

    private void drawTextStat(Graphics2D g2d, String stat, int infoLevel) {
        g2d.setFont(new Font("Arial", Font.BOLD, TEXT_SIZE));
        FontMetrics metrics = g2d.getFontMetrics();
        int y = metrics.getAscent() + TEXT_MARGIN * infoLevel;
        g2d.setColor(Color.BLACK);
        g2d.drawString(stat, TEXT_MARGIN, y);
    }

    private void drawStatBar(Graphics2D g2d, double current, double max, int infoLevel) {

        int x = TEXT_MARGIN;
        int y = TEXT_MARGIN * infoLevel;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, TEXT_SIZE * 7, TEXT_MARGIN);

        double portion = Math.max(0, Math.min(1, current / max));

        g2d.setColor(new Color(
                (int) (255 * portion),
                (int) (255 * (1 - portion)),
                (int) (100 * (1 - portion))
        ));

        g2d.fillRect(x + 2, y + 2,
                (int) (portion * TEXT_SIZE * 7) - 2,
                TEXT_MARGIN - 2);
    }

    private void drawTitle(Graphics2D g2d, String title, Color color, int vw, int vh) {

        g2d.setFont(new Font("Arial", Font.BOLD, TEXT_SIZE * 2));

        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(title);

        int x = (vw - textWidth) / 2;
        int y = vh / 5;

        g2d.setColor(color);
        g2d.drawString(title, x, y);
    }

    private void drawGameStats(Graphics2D g2d) {

        String scoreText;

        if (gameBase.getGameModeType() == GameModeType.Endless) {
            scoreText = "Distance Traveled: " +
                    (int) gameBase.getPlayer().getDistanceTraveled() / 100;
        } else {
            scoreText = "Time: " + gameBase.getFormattedGameTime();
        }

        Font scoreFont = new Font("Arial", Font.BOLD, 40);
        g2d.setFont(scoreFont);

        FontMetrics metrics = g2d.getFontMetrics(scoreFont);

        int textWidth = metrics.stringWidth(scoreText);
        int textHeight = metrics.getAscent();

        int x = gameBase.getWidth() / 2 - textWidth / 2;
        int y = gameBase.getHeight() / 4 + TEXT_MARGIN;

        g2d.setColor(Color.WHITE);
        g2d.drawString(scoreText, x, y);

        int yOffset = y + textHeight * 2;

        Font highscoreFont = new Font("Arial", Font.PLAIN, 20);
        g2d.setFont(highscoreFont);

        FontMetrics scoreMetrics = g2d.getFontMetrics(highscoreFont);

        HighscoreList highscoreList = highscoreLists.get(gameBase.getGameModeType());

        if (highscoreList == null) return;

        List<Highscore> highscores = highscoreList.getHighscores();
        int amountToDraw = Math.min(10, highscores.size());

        for (int i = 0; i < amountToDraw; i++) {

            Highscore hs = highscores.get(i);

            String valueText;

            if (gameBase.getGameModeType() == GameModeType.Endless) {
                valueText = String.valueOf(hs.getPoints());
            } else {
                if (hs.isDNF()) {
                    valueText = "DNF";
                } else {
                    valueText = formatTime(hs.getPoints());
                }
            }

            String text = hs.getName() + " - " + valueText;

            int scoreTextWidth = scoreMetrics.stringWidth(text);
            int scoreX = gameBase.getWidth() / 2 - scoreTextWidth / 2;

            g2d.drawString(text, scoreX, yOffset);

            yOffset += scoreMetrics.getHeight();
        }
    }

    private String formatTime(int centiseconds) {

        int minutes = centiseconds / 6000;
        int seconds = (centiseconds / 100) % 60;
        int hundredths = centiseconds % 100;

        return String.format("%02d:%02d.%02d", minutes, seconds, hundredths);
    }

    private Point convertToVirtual(Point screenPoint) {

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int vw = gameBase.getWidth();
        int vh = gameBase.getHeight();

        double scaleX = (double) panelWidth / vw;
        double scaleY = (double) panelHeight / vh;
        double scale = Math.min(scaleX, scaleY);

        int offsetX = (int) ((panelWidth - vw * scale) / 2);
        int offsetY = (int) ((panelHeight - vh * scale) / 2);

        int sx = screenPoint.x;
        int sy = screenPoint.y;

        if (sx < offsetX || sx >= offsetX + vw * scale ||
                sy < offsetY || sy >= offsetY + vh * scale) {
            return null;
        }

        int vx = (int) ((sx - offsetX) / scale);
        int vy = (int) ((sy - offsetY) / scale);

        return new Point(vx, vy);
    }
}