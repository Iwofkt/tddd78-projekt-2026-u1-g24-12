package se.liu.simjolucul.dopeslope.game;

import se.liu.simjolucul.dopeslope.Main;
import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.gameobjects.Obstacle;
import se.liu.simjolucul.dopeslope.gameobjects.Player;
import se.liu.simjolucul.dopeslope.handlers.GameTimer;      // <-- added import
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

/**
 * Main game rendering component. Handles scaling of the game world,
 * drawing of all game objects, particles, UI overlays, and pause/game‑over screens.
 */
public class GameComponent extends JComponent implements GameObserver {

    // -- Layout and UI constants -- //
    private static final int TEXT_MARGIN = 10;
    private static final int TEXT_SIZE = 20;
    private static final float MENU_TRANSPARENCY = 0.4F;

    // Pause menu buttons
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_VERTICAL_SPACING = 60;

    // This constant needs to be a fraction – use a double or two ints.
    // For simplicity, keep the original two‑int approach.
    private static final int BUTTON_Y_OFFSET_NUM = 2;
    private static final int BUTTON_Y_OFFSET_DEN = 3;

    // Stat display
    /** Multiplier for TEXT_SIZE to get stat bar width. */
    private static final int STAT_BAR_WIDTH_FACTOR = 7;
    /** Inner inset for the filled portion of the stat bar. */
    private static final int STAT_BAR_INNER_INSET = 2;
    /** Green component base for low‑speed color. */
    private static final int STAT_BAR_GREEN_COMPONENT = 100;

    // Gradient overlay
    /** Offset from bottom where the gradient ends. */
    private static final int GRADIENT_BOTTOM_OFFSET = 50;
    private static final Color GRADIENT_TOP_COLOR = new Color(35, 105, 158, 20);
    private static final Color GRADIENT_BOTTOM_COLOR = new Color(0, 0, 0, 50);

    // Title
    /** Multiplier for TEXT_SIZE to get title font size. */
    private static final int TITLE_FONT_SIZE_MULTIPLIER = 2;
    /** Denominator for vertical title position (vh / 5). */
    private static final int TITLE_Y_FRACTION_DEN = 5;

    // Game over / paused stats
    private static final int SCORE_FONT_SIZE = 40;
    private static final int HIGHSCORE_FONT_SIZE = 20;
    /** Maximum number of highscores to display. */
    private static final int HIGHSCORE_DISPLAY_LIMIT = 10;

    // Stat text positions (infoLevel values)
    private static final int STAT_SPEED_LABEL_LINE = 1;
    private static final int STAT_SPEED_BAR_LINE = 4;
    private static final int STAT_DISTANCE_LABEL_LINE = 6;
    private static final int STAT_DISTANCE_VALUE_LINE = 8;
    private static final int STAT_TIME_LABEL_LINE = 11;
    private static final int STAT_TIME_VALUE_LINE = 13;

    // Other numeric constants
    /** Divisor to convert distance to metres (distance is stored in cm). */
    private static final int CENTIMETERS_PER_METER = 100;   // fixed spelling
    /** Factor for spacing after main score. */
    private static final int SCORE_TO_HIGHSCORE_SPACING = 2;
    /** Vertical position fraction for main score (1/4 of screen height). */
    private static final int SCORE_Y_FRACTION_DEN = 4;

    private final Map<GameModeType, HighscoreList> highscoreLists;
    private final GameBase gameBase;
    private final List<Button> pauseMenuButtons = new ArrayList<>();

    /**
     * Creates a new GameComponent.
     *
     * @param gameBase        the game logic instance
     * @param main            the main application (for menu navigation)
     * @param highscoreLists  map of highscore lists per game mode
     */
    public GameComponent(GameBase gameBase, Main main, Map<GameModeType, HighscoreList> highscoreLists) {
        this.gameBase = gameBase;
        this.highscoreLists = highscoreLists;

        gameBase.addObserver(this);

        // Position buttons relative to the game world size
        int centerX = (gameBase.getWidth() - BUTTON_WIDTH) / 2;
        int baseY = gameBase.getHeight() * BUTTON_Y_OFFSET_NUM / BUTTON_Y_OFFSET_DEN;

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
        ViewTransform transform = computeViewTransform();
        g2d.translate(transform.offsetX, transform.offsetY);
        g2d.scale(transform.scale, transform.scale);

        Player player = gameBase.getPlayer();

        // Background and base layers
        overlay(g2d, 1.0f, Color.LIGHT_GRAY, transform.vw, transform.vh);

        // Draw all effect layers in correct order
        drawParticles(g2d, gameBase.getTrackParticles());
        player.draw(g2d);
        drawShadowsAndObjects(g2d, gameBase.getObstacles(), player);
        drawShadowsAndObjects(g2d, gameBase.getGates(), player);
        drawShadowsAndObjects(g2d, gameBase.getFinishLine(), player);
        drawParticles(g2d, gameBase.getSprayParticles());

        // Atmospheric gradient
        drawAtmosphericGradient(g2d, transform.vw, transform.vh);

        drawParticles(g2d, gameBase.getSnowParticles());

        // UI stats
        drawTextStat(g2d, "SPEED:", STAT_SPEED_LABEL_LINE);
        drawStatBar(g2d, player.getYSpeed(), player.getMaxSpeed(), STAT_SPEED_BAR_LINE);

        drawTextStat(g2d, "DISTANCE:", STAT_DISTANCE_LABEL_LINE);
        int distanceMetres = (int) (player.getDistanceTraveled() / CENTIMETERS_PER_METER);
        drawTextStat(g2d, distanceMetres + " m", STAT_DISTANCE_VALUE_LINE);

        drawTextStat(g2d, "TIME:", STAT_TIME_LABEL_LINE);
        drawTextStat(g2d, gameBase.getFormattedGameTime(), STAT_TIME_VALUE_LINE);

        // Game over / pause overlay
        if (gameBase.isGameOver() || gameBase.isGamePaused()) {
            Color overlayColor = gameBase.isGameOver() ? new Color(70, 0, 0) : new Color(0, 0, 70);
            overlay(g2d, MENU_TRANSPARENCY, overlayColor, transform.vw, transform.vh);

            drawTitle(g2d,
                      gameBase.isGameOver() ? "GAME OVER" : "GAME PAUSED",
                      gameBase.isGameOver() ? Color.RED : Color.BLUE,
                      transform.vw, transform.vh);

            drawGameStats(g2d);

            pauseMenuButtons.forEach(btn -> btn.draw(g2d));
        }

        g2d.dispose();
    }

    /**
     * Helper record holding the result of view transformation calculations.
     */
    private record ViewTransform(int vw, int vh, double scale, int offsetX, int offsetY) {}

    /**
     * Computes the scaling and offsets needed to fit the virtual game world
     * into the actual component size.
     */
    private ViewTransform computeViewTransform() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int vw = gameBase.getWidth();
        int vh = gameBase.getHeight();

        double scaleX = (double) panelWidth / vw;
        double scaleY = (double) panelHeight / vh;
        double scale = Math.min(scaleX, scaleY);

        int offsetX = (int) ((panelWidth - vw * scale) / 2);
        int offsetY = (int) ((panelHeight - vh * scale) / 2);

        return new ViewTransform(vw, vh, scale, offsetX, offsetY);
    }

    private <T extends Obstacle> void drawShadowsAndObjects(Graphics2D g2d, List<T> objects, Player player) {
        objects.forEach(obj -> obj.drawShadow(g2d));
        objects.forEach(obj -> obj.draw(g2d, player.getCurrentSpeed()));
    }

    private void drawParticles(Graphics2D g2d, List<? extends Particle> particles) {
        particles.forEach(p -> p.draw(g2d));
    }

    /**
     * Fills the entire virtual view with a semi‑transparent overlay.
     */
    private void overlay(Graphics2D g2d, float transparency, Color color, int vw, int vh) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
        g2d.setColor(color);
        g2d.fillRect(0, 0, vw, vh);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    private void drawAtmosphericGradient(Graphics2D g2d, int vw, int vh) {
        GradientPaint gradient = new GradientPaint(
                0, 0, GRADIENT_TOP_COLOR,
                0, vh - GRADIENT_BOTTOM_OFFSET, GRADIENT_BOTTOM_COLOR
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, vw, vh);
    }

    /**
     * Draws a text statistic at a given line number.
     */
    private void drawTextStat(Graphics2D g2d, String stat, int line) {
        g2d.setFont(new Font("Arial", Font.BOLD, TEXT_SIZE));
        FontMetrics metrics = g2d.getFontMetrics();
        int y = metrics.getAscent() + TEXT_MARGIN * line;
        g2d.setColor(Color.BLACK);
        g2d.drawString(stat, TEXT_MARGIN, y);
    }

    /**
     * Draws a colored bar representing a numeric value.
     */
    private void drawStatBar(Graphics2D g2d, double current, double max, int line) {
        int x = TEXT_MARGIN;
        int y = TEXT_MARGIN * line;

        // Background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, TEXT_SIZE * STAT_BAR_WIDTH_FACTOR, TEXT_MARGIN);

        double portion = Math.max(0, Math.min(1, current / max));

        // Foreground (colour changes with speed)
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
        GameModeType mode = gameBase.getGameModeType();
        boolean isEndless = (mode == GameModeType.ENDLESS);

        String scoreText;
        if (isEndless) {
            int distanceMetres = (int) (gameBase.getPlayer().getDistanceTraveled() / CENTIMETERS_PER_METER);
            scoreText = "Distance Traveled: " + distanceMetres;
        } else {
            scoreText = "Time: " + gameBase.getFormattedGameTime();
        }

        // Main score
        g2d.setFont(new Font("Arial", Font.BOLD, SCORE_FONT_SIZE));
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(scoreText);
        int textHeight = metrics.getAscent();
        int x = gameBase.getWidth() / 2 - textWidth / 2;
        int y = gameBase.getHeight() / SCORE_Y_FRACTION_DEN + TEXT_MARGIN;
        g2d.setColor(Color.WHITE);
        g2d.drawString(scoreText, x, y);

        // Highscores list
        int yOffset = y + textHeight * SCORE_TO_HIGHSCORE_SPACING;
        g2d.setFont(new Font("Arial", Font.PLAIN, HIGHSCORE_FONT_SIZE));
        FontMetrics smallMetrics = g2d.getFontMetrics();

        HighscoreList highscoreList = highscoreLists.get(mode);
        if (highscoreList == null) return;

        List<Highscore> highscores = highscoreList.getHighscores();
        int amountToDraw = Math.min(HIGHSCORE_DISPLAY_LIMIT, highscores.size());

        for (int i = 0; i < amountToDraw; i++) {
            Highscore hs = highscores.get(i);
            String valueText;
            if (isEndless) {
                valueText = String.valueOf(hs.getPoints());
            } else {
                // Use the static formatter from GameTimer
                valueText = hs.isDNF() ? "DNF" : GameTimer.formatMillis(hs.getPoints());
            }
            String line = hs.getName() + " - " + valueText;
            int lineWidth = smallMetrics.stringWidth(line);
            int lineX = gameBase.getWidth() / 2 - lineWidth / 2;
            g2d.drawString(line, lineX, yOffset);
            yOffset += smallMetrics.getHeight();
        }
    }

    /**
     * Converts screen coordinates to virtual world coordinates.
     *
     * @param screenPoint point in screen pixel coordinates
     * @return corresponding point in virtual world, or {@code null} if outside the game view
     */
    private Point convertToVirtual(Point screenPoint) {
        ViewTransform t = computeViewTransform();
        int sx = screenPoint.x;
        int sy = screenPoint.y;

        if (sx < t.offsetX || sx >= t.offsetX + t.vw * t.scale ||
            sy < t.offsetY || sy >= t.offsetY + t.vh * t.scale) {
            return null; // outside the game view
        }

        int vx = (int) ((sx - t.offsetX) / t.scale);
        int vy = (int) ((sy - t.offsetY) / t.scale);
        return new Point(vx, vy);
    }
}