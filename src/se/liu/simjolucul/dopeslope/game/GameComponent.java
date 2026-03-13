package se.liu.simjolucul.dopeslope.game;

import se.liu.simjolucul.dopeslope.Main;
import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.gameobjects.Obstacle;
import se.liu.simjolucul.dopeslope.gameobjects.Player;
import se.liu.simjolucul.dopeslope.highscore.Highscore;
import se.liu.simjolucul.dopeslope.highscore.HighscoreList;
import se.liu.simjolucul.dopeslope.ui.Button;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameComponent extends JComponent implements GameObserver {

    // --- Layout and UI constants ---
    private static final int TEXT_MARGIN = 10;
    private static final int TEXT_SIZE = 20;
    private static final float MENU_TRANSPARENCY = 0.4F;

    // Pause menu buttons
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_VERTICAL_SPACING = 60;
    private static final int BUTTON_Y_OFFSET_FRACTION_NUM = 2; // numerator for 2/3
    private static final int BUTTON_Y_OFFSET_FRACTION_DEN = 3; // denominator for 2/3

    // Stat display
    private static final int STAT_BAR_WIDTH_FACTOR = 7;          // TEXT_SIZE * 7
    private static final int STAT_BAR_INNER_INSET = 2;           // for the filled portion
    private static final int STAT_BAR_GREEN_COMPONENT = 100;     // green base when speed low

    // Gradient overlay
    private static final int GRADIENT_BOTTOM_OFFSET = 50;
    private static final Color GRADIENT_TOP_COLOR = new Color(35, 105, 158, 20);
    private static final Color GRADIENT_BOTTOM_COLOR = new Color(0, 0, 0, 50);

    // Title
    private static final int TITLE_FONT_SIZE_MULTIPLIER = 2;     // TEXT_SIZE * 2
    private static final int TITLE_Y_FRACTION_DEN = 5;           // vh / 5

    // Game over / paused stats
    private static final int SCORE_FONT_SIZE = 40;
    private static final int HIGHSCORE_FONT_SIZE = 20;
    private static final int HIGHSCORE_DISPLAY_LIMIT = 10;

    // Stat text positions (infoLevel values)
    private static final int STAT_SPEED_LABEL_LINE = 1;
    private static final int STAT_SPEED_BAR_LINE = 4;
    private static final int STAT_DISTANCE_LABEL_LINE = 6;
    private static final int STAT_DISTANCE_VALUE_LINE = 8;
    private static final int STAT_TIME_LABEL_LINE = 11;
    private static final int STAT_TIME_VALUE_LINE = 13;

    // Time formatting (centiseconds = 1/100 second)
    private static final int CENTISECONDS_PER_MINUTE = 6000;   // 60 * 100
    private static final int CENTISECONDS_PER_SECOND = 100;
    private static final int SECONDS_PER_MINUTE = 60;

    private final Map<GameModeType, HighscoreList> highscoreLists;
    private final GameBase gameBase;

    private final List<Button> pauseMenuButtons = new ArrayList<>();

    public GameComponent(GameBase gameBase, Main main, Map<GameModeType, HighscoreList> highscoreLists) {
        this.gameBase = gameBase;
        this.highscoreLists = highscoreLists;

        gameBase.addObserver(this);

        // Position buttons relative to the game world size
        int centerX = (gameBase.getWidth() - BUTTON_WIDTH) / 2;
        int baseY = gameBase.getHeight() * BUTTON_Y_OFFSET_FRACTION_NUM / BUTTON_Y_OFFSET_FRACTION_DEN;

        pauseMenuButtons.add(new Button(
                centerX, baseY, BUTTON_WIDTH, BUTTON_HEIGHT,
                "RESTART",
                () -> gameBase.restart(gameBase.getGameModeType())
        ));

        pauseMenuButtons.add(new Button(
                centerX, baseY + BUTTON_VERTICAL_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT,
                "MAIN MENU",
                main::showMenu
        ));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point virtual = convertToVirtual(e.getPoint());
                pauseMenuButtons.forEach(btn -> btn.setHovered(virtual));
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Point virtual = convertToVirtual(e.getPoint());
                pauseMenuButtons.forEach(btn -> btn.handleClick(virtual));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pauseMenuButtons.forEach(btn -> btn.setHovered(null));
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

        // Calculate scaling to fit the game world into the component
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

        // Background and base layers
        overlay(g2d, 1, Color.LIGHT_GRAY, vw, vh);

        // Draw all effect layers in correct order
        drawParticles(g2d, gameBase.getTrackParticles());
        player.draw(g2d);
        drawShadowsAndObjects(g2d, gameBase.getObstacles(), player);
        drawShadowsAndObjects(g2d, gameBase.getGates(), player);
        drawShadowsAndObjects(g2d, gameBase.getFinishLine(), player);
        drawParticles(g2d, gameBase.getSprayParticles());

        // Atmospheric gradient
        drawAtmosphericGradient(g2d, vw, vh);

        drawParticles(g2d, gameBase.getSnowParticles());

        // UI stats
        drawTextStat(g2d, "SPEED:", STAT_SPEED_LABEL_LINE);
        drawStatBar(g2d, player.getYSpeed(), player.getMaxSpeed(), STAT_SPEED_BAR_LINE);

        drawTextStat(g2d, "DISTANCE:", STAT_DISTANCE_LABEL_LINE);
        drawTextStat(g2d, (int) (player.getDistanceTraveled() / 100) + " m", STAT_DISTANCE_VALUE_LINE);

        drawTextStat(g2d, "TIME:", STAT_TIME_LABEL_LINE);
        drawTextStat(g2d, gameBase.getFormattedGameTime(), STAT_TIME_VALUE_LINE);

        // Game over / pause overlay
        if (gameBase.isGameOver() || gameBase.isGamePaused()) {
            Color overlayColor = gameBase.isGameOver() ? new Color(70, 0, 0) : new Color(0, 0, 70);
            overlay(g2d, MENU_TRANSPARENCY, overlayColor, vw, vh);

            drawTitle(g2d,
                      gameBase.isGameOver() ? "GAME OVER" : "GAME PAUSED",
                      gameBase.isGameOver() ? Color.RED : Color.BLUE,
                      vw, vh);

            drawGameStats(g2d);

            pauseMenuButtons.forEach(btn -> btn.draw(g2d));
        }

        g2d.dispose();
    }

    // Helper to draw a list of drawable objects with shadows and then the objects themselves
    private <T extends Obstacle> void drawShadowsAndObjects(Graphics2D g2d, List<T> objects, Player player) {
        objects.forEach(obj -> obj.drawShadow(g2d));
        objects.forEach(obj -> obj.draw(g2d, player.getCurrentSpeed()));
    }

    // Helper to draw a list of particles
    private void drawParticles(Graphics2D g2d, List<? extends Particle> particles) {
        particles.forEach(p -> p.draw(g2d));
    }

    private void overlay(Graphics2D g2d, float transparency, Color color, int vw, int vh) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
        g2d.setColor(color);
        g2d.fillRect(0, 0, vw, vh);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }

    private void drawAtmosphericGradient(Graphics2D g2d, int vw, int vh) {
        GradientPaint gradient = new GradientPaint(
                0, 0, GRADIENT_TOP_COLOR,
                0, vh - GRADIENT_BOTTOM_OFFSET, GRADIENT_BOTTOM_COLOR
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, vw, vh);
    }

    private void drawTextStat(Graphics2D g2d, String stat, int line) {
        g2d.setFont(new Font("Arial", Font.BOLD, TEXT_SIZE));
        FontMetrics metrics = g2d.getFontMetrics();
        int y = metrics.getAscent() + TEXT_MARGIN * line;
        g2d.setColor(Color.BLACK);
        g2d.drawString(stat, TEXT_MARGIN, y);
    }

    private void drawStatBar(Graphics2D g2d, double current, double max, int line) {
        int x = TEXT_MARGIN;
        int y = TEXT_MARGIN * line;

        // Background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, TEXT_SIZE * STAT_BAR_WIDTH_FACTOR, TEXT_MARGIN);

        double portion = Math.max(0, Math.min(1, current / max));

        // Foreground (color changes with speed)
        g2d.setColor(new Color(
                (int) (255 * portion),
                (int) (255 * (1 - portion)),
                (int) (STAT_BAR_GREEN_COMPONENT * (1 - portion))
        ));

        int fillWidth = (int) (portion * TEXT_SIZE * STAT_BAR_WIDTH_FACTOR) - STAT_BAR_INNER_INSET;
        g2d.fillRect(x + STAT_BAR_INNER_INSET,
                     y + STAT_BAR_INNER_INSET,
                     fillWidth,
                     TEXT_MARGIN - STAT_BAR_INNER_INSET);
    }

    private void drawTitle(Graphics2D g2d, String title, Color color, int vw, int vh) {
        g2d.setFont(new Font("Arial", Font.BOLD, TEXT_SIZE * TITLE_FONT_SIZE_MULTIPLIER));
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(title);
        int x = (vw - textWidth) / 2;
        int y = vh / TITLE_Y_FRACTION_DEN;
        g2d.setColor(color);
        g2d.drawString(title, x, y);
    }

    private void drawGameStats(Graphics2D g2d) {
        String scoreText;
        if (gameBase.getGameModeType() == GameModeType.ENDLESS) {
            scoreText = "Distance Traveled: " +
                        (int) gameBase.getPlayer().getDistanceTraveled() / 100;
        } else {
            scoreText = "Time: " + gameBase.getFormattedGameTime();
        }

        // Main score
        g2d.setFont(new Font("Arial", Font.BOLD, SCORE_FONT_SIZE));
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(scoreText);
        int textHeight = metrics.getAscent();
        int x = gameBase.getWidth() / 2 - textWidth / 2;
        int y = gameBase.getHeight() / 4 + TEXT_MARGIN;
        g2d.setColor(Color.WHITE);
        g2d.drawString(scoreText, x, y);

        // Highscores list
        int yOffset = y + textHeight * 2;
        g2d.setFont(new Font("Arial", Font.PLAIN, HIGHSCORE_FONT_SIZE));
        FontMetrics smallMetrics = g2d.getFontMetrics();

        HighscoreList highscoreList = highscoreLists.get(gameBase.getGameModeType());
        if (highscoreList == null) return;

        List<Highscore> highscores = highscoreList.getHighscores();
        int amountToDraw = Math.min(HIGHSCORE_DISPLAY_LIMIT, highscores.size());

        for (int i = 0; i < amountToDraw; i++) {
            Highscore hs = highscores.get(i);
            String valueText;
            if (gameBase.getGameModeType() == GameModeType.ENDLESS) {
                valueText = String.valueOf(hs.getPoints());
            } else {
                valueText = hs.isDNF() ? "DNF" : formatTime(hs.getPoints());
            }
            String line = hs.getName() + " - " + valueText;
            int lineWidth = smallMetrics.stringWidth(line);
            int lineX = gameBase.getWidth() / 2 - lineWidth / 2;
            g2d.drawString(line, lineX, yOffset);
            yOffset += smallMetrics.getHeight();
        }
    }

    // Format centiseconds (1/100 s) as MM:SS.hh
    private String formatTime(int centiseconds) {
        int minutes = centiseconds / CENTISECONDS_PER_MINUTE;
        int seconds = (centiseconds / CENTISECONDS_PER_SECOND) % SECONDS_PER_MINUTE;
        int hundredths = centiseconds % CENTISECONDS_PER_SECOND;
        return String.format("%02d:%02d.%02d", minutes, seconds, hundredths);
    }

    // Convert screen coordinates to virtual world coordinates (or null if outside)
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
            return null; // outside the game view
        }

        int vx = (int) ((sx - offsetX) / scale);
        int vy = (int) ((sy - offsetY) / scale);
        return new Point(vx, vy);
    }
}