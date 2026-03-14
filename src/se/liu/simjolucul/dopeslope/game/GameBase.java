package se.liu.simjolucul.dopeslope.game;

import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.slopes.CombeDeCaron;
import se.liu.simjolucul.dopeslope.slopes.Endless;
import se.liu.simjolucul.dopeslope.slopes.GameMode;
import se.liu.simjolucul.dopeslope.effects.snowfx.SnowFall;
import se.liu.simjolucul.dopeslope.effects.snowfx.SnowSpray;
import se.liu.simjolucul.dopeslope.effects.track.Tracks;
import se.liu.simjolucul.dopeslope.effects.track.TrackParticle;
import se.liu.simjolucul.dopeslope.gameobjects.*;
import se.liu.simjolucul.dopeslope.handlers.GameTimer;
import se.liu.simjolucul.dopeslope.handlers.ImageLoader;
import se.liu.simjolucul.dopeslope.handlers.collision.ObjectCollision;
import se.liu.simjolucul.dopeslope.handlers.interaction.InputHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Central controller for the game logic. Manages the game world, player,
 * obstacles, gates, finish line, particle effects, and game state.
 * <p>
 * Responsibilities include:
 * <ul>
 *   <li>Updating the player position and rotation based on input</li>
 *   <li>Managing different game modes (endless, alpine) via {@link GameMode}</li>
 *   <li>Handling collision detection and game over conditions</li>
 *   <li>Spawning and updating visual effects (snowfall, snow spray, player tracks)</li>
 *   <li>Maintaining game time via {@link GameTimer}</li>
 *   <li>Notifying observers (e.g., {@link GameComponent}) when the world changes</li>
 *   <li>Resetting the game state when restarting</li>
 * </ul>
 * <p>
 * The game world size is fixed by the constructor parameters. The class interacts
 * with an {@link InputHandler} to process player controls, and with a {@link GameMode}
 * to generate obstacles and gates specific to the selected mode.
 *
 * @see GameMode
 * @see GameObserver
 * @see Player
 * @see GameTimer
 */
public class GameBase {

    // Constants (all private and final)
    private static final int PLAYER_TEXTURE_SCALE = 2;
    private static final double PLAYER_START_ROTATION = 0.5 * Math.PI;
    private static final int MARGIN = 30;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final double SPRAY_THRESHOLD_SPEED = 8.0;
    private final static String RESOURCE_PACK = "standardPixel";

    private final int width;
    private final int height;

    // Made private to avoid package visibility
    private GameModeType gameModeType = GameModeType.ENDLESS;

    private final Player player;
    private final GameTimer gameTimer = new GameTimer();
    private final ObjectCollision objectCollision = new ObjectCollision();
    private InputHandler inputHandler = null;
    private GameMode gameMode = null;

    private final SnowFall snowFall;
    private final Tracks playerTracks;
    private final SnowSpray snowSpray;

    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Gate> gates = new ArrayList<>();
    private final List<FinishLine> finishLine = new ArrayList<>();

    private boolean gameOver = false;
    private boolean gamePaused = false;
    private boolean newGameFlag = false;
    private boolean finishedRace = false;

    private final List<GameObserver> observers;

    // ===== Constructor =====

    public GameBase(int width, int height) {
        this.height = height;
        this.width = width;

        Point spawnPoint = new Point(width / 2, height / 3);

        final BufferedImage playerImg = ImageLoader.loadTextureSize(
                getResourcePack(), "player", PLAYER_TEXTURE_SCALE, PLAYER_TEXTURE_SCALE);
        this.player = new Player(spawnPoint, PLAYER_START_ROTATION, playerImg);

        this.observers = new ArrayList<>();

        if (gameModeType == GameModeType.ENDLESS) {
            gameMode = new Endless(this);
        } else if (gameModeType == GameModeType.COMBE_DE_CARON) {
            gameMode = new CombeDeCaron(this);
        }

        snowFall = new SnowFall(1, width);
        snowFall.initializeSnowfall(width, height);

        snowSpray = new SnowSpray(3.0, 11.8);
        playerTracks = new Tracks(20);
    }

    // ===== Getters =====

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isGamePaused() {
        return gamePaused;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<Gate> getGates() {
        return gates;
    }

    public List<FinishLine> getFinishLine() {
        return finishLine;
    }

    public String getResourcePack() {
        return RESOURCE_PACK;
    }

    public List<Particle> getSnowParticles() {
        return snowFall.getParticles();
    }

    public List<TrackParticle> getTrackParticles() {
        return playerTracks.getParticles();
    }

    public List<SnowSpray.SprayParticle> getSprayParticles() {
        return snowSpray.getParticles();
    }

    public int getMargin() {
        return MARGIN;
    }

    public String getFormattedGameTime() {
        return GameTimer.formatMillis(gameTimer.getTimeInMillis());
    }

    public boolean hasFinishedRace() {
        return finishedRace;
    }

    public int getElapsedMilliseconds() {
        return (int) (gameTimer.getTime() * MILLISECONDS_PER_SECOND);
    }

    // ===== Setters =====

    public void setGameOver(boolean gameOver) {
        if (gameOver) {
            gameTimer.pause();
        }
        this.gameOver = gameOver;
        notifyObservers();
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    public void setFinishedRace(boolean finishedRace) {
        this.finishedRace = finishedRace;
    }

    public void setGamePaused(boolean gamePaused) {
        this.gamePaused = gamePaused;

        if (gamePaused) {
            gameTimer.pause();
        } else {
            gameTimer.start();
        }

        notifyObservers();
    }

    // ====== Game Update ======

    public void update() {
        if (isGameOver() || isGamePaused()) return;

        player.update();
        player.moveHorizontally(width, MARGIN);

        if (inputHandler != null) {
            if (inputHandler.isKeyPressed(Direction.LEFT)) {
                player.rotate(Direction.LEFT);
            }
            if (inputHandler.isKeyPressed(Direction.RIGHT)) {
                player.rotate(Direction.RIGHT);
            }
            if (inputHandler.isQuitPressed()) {
                System.exit(0);
            }
            inputHandler.checkActions();
        }

        gameMode.update();

        handleCollision();
        removeOffScreenObjects();

        updateParticles();

        notifyObservers();
    }

    private void updateParticles() {
        double ySpeed = player.getSpeed().getY();
        double xSpeed = player.getSpeed().getX();
        snowFall.update((int) player.getCurrentSpeed());
        playerTracks.update((int) ySpeed);

        double moveAngle = Math.atan2(ySpeed, xSpeed);

        playerTracks.spawnTracks(player, moveAngle);

        if (player.getCurrentSpeed() > SPRAY_THRESHOLD_SPEED) {
            Point[] tips = player.getSkiTipPositions();

            for (Point tip : tips) {
                snowSpray.spawn(tip.x, tip.y, player.getCurrentSpeed(), moveAngle);
            }
        }

        snowSpray.update((int) ySpeed);
    }

    private void handleCollision() {
        for (Obstacle obstacle : obstacles) {
            if (objectCollision.isCollisionDetected(player, obstacle)) {
                setGameOver(true);
            }
        }
    }

    private void removeOffScreenObjects() {
        double ySpeed = player.getSpeed().getY();
        Iterator<Obstacle> itO = obstacles.iterator();
        while (itO.hasNext()) {
            Obstacle o = itO.next();
            o.update(ySpeed);

            if (o.getPosition().y + o.getHeight() < 0) {
                itO.remove();
            }
        }

        Iterator<Gate> itG = gates.iterator();
        while (itG.hasNext()) {
            Gate g = itG.next();
            g.update(ySpeed);

            if (g.getPosition().y + g.getHeight() < 0) {
                itG.remove();
            }
        }

        Iterator<FinishLine> itF = finishLine.iterator();   // updated field name
        while (itF.hasNext()) {
            FinishLine f = itF.next();
            f.update(ySpeed);

            if (f.getPosition().y + f.getHeight() < 0) {
                itF.remove();
            }
        }
    }

    // ===== Observers =====

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for (GameObserver observer : observers) {
            observer.worldUpdated();
        }
    }

    // ===== Game Reset =====

    public void restart(GameModeType gameModeType) {
        gameOver = false;
        gamePaused = false;
        newGameFlag = true;
        finishedRace = false;
        this.gameModeType = gameModeType;

        // Null check to avoid potential NullPointerException
        if (inputHandler != null) {
            inputHandler.setLeftPressed(false);
            inputHandler.setRightPressed(false);
        }

        obstacles.clear();
        gates.clear();
        finishLine.clear();           // updated field name

        gameTimer.reset();
        gameTimer.start();

        Point spawnPoint = new Point(width / 2, height / 3);
        double startRotation = 0.5 * Math.PI;

        player.reset(spawnPoint, startRotation);

        if (gameModeType == GameModeType.ENDLESS) {
            gameMode = new Endless(this);
        } else if (gameModeType == GameModeType.COMBE_DE_CARON) {
            gameMode = new CombeDeCaron(this);
        }

        playerTracks.clear();
        snowSpray.clear();
        snowFall.reset();

        notifyObservers();
    }

    /**
     * Returns true if a new game was started since the last call to this method,
     * and resets the flag. Used by the UI to detect when to reinitialize views.
     */
    public boolean pollNewGameFlag() {
        boolean wasNewGame = newGameFlag;
        newGameFlag = false;
        return wasNewGame;
    }

    public GameModeType getGameModeType() {
        return gameModeType;
    }
}