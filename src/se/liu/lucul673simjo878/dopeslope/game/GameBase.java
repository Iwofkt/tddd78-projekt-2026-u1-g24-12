package se.liu.simjolucul.dopeslope.game;

import se.liu.simjolucul.dopeslope.slopes.CombeDeCaron;
import se.liu.simjolucul.dopeslope.slopes.Endless;
import se.liu.simjolucul.dopeslope.slopes.GameMode;
import se.liu.simjolucul.dopeslope.effects.snowfx.SnowFall;
import se.liu.simjolucul.dopeslope.effects.snowfx.SnowParticle;
import se.liu.simjolucul.dopeslope.effects.snowfx.SnowSpray;
import se.liu.simjolucul.dopeslope.effects.track.Tracks;
import se.liu.simjolucul.dopeslope.effects.track.TrackParticle;
import se.liu.simjolucul.dopeslope.gameObjects.*;
import se.liu.simjolucul.dopeslope.handlers.GameTimer;
import se.liu.simjolucul.dopeslope.handlers.ImageLoader;
import se.liu.simjolucul.dopeslope.handlers.collision.ObjectCollision;
import se.liu.simjolucul.dopeslope.handlers.interaction.InputHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameBase {

    // Constants (all private and final)
    private static final int PLAYER_TEXTURE_SCALE = 2;
    private static final double PLAYER_START_ROTATION = 0.5 * Math.PI;
    private static final int MARGIN = 30;
    private static final int CENTISECONDS_PER_SECOND = 100;
    private static final double SPRAY_THRESHOLD_SPEED = 8.0;
    private final static String RESOURCE_PACK = "standardPixel";

    private final int width;
    private final int height;

    // Made private to avoid package visibility
    private GameModeType gameModeType = GameModeType.Endless;

    private final Player player;
    private final GameTimer gameTimer = new GameTimer();
    private final ObjectCollision objectCollision = new ObjectCollision();
    private InputHandler inputHandler = null;
    private GameMode gameMode = null;               // made private

    private final SnowFall snowFall;
    private final Tracks playerTracks;
    private final SnowSpray snowSpray;

    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Gate> gates = new ArrayList<>();
    private final List<Finishline> finishLine = new ArrayList<>();   // renamed from finishline (camelCase)

    private boolean gameOver = false;
    private boolean gamePaused = false;
    private boolean newGameFlag = false;            // renamed from newGame to avoid conflict with method
    private boolean finishedRace = false;

    private final List<GameObserver> observers;

    //-- CONSTRUCTOR --//

    public GameBase(int width, int height) {
        this.height = height;
        this.width = width;

        Point spawnPoint = new Point(width / 2, height / 3);

        final BufferedImage playerImg = ImageLoader.loadTextureSize(
                getResourcePack(), "player", PLAYER_TEXTURE_SCALE, PLAYER_TEXTURE_SCALE);
        this.player = new Player(spawnPoint, PLAYER_START_ROTATION, playerImg);

        this.observers = new ArrayList<>();

        if (gameModeType == GameModeType.Endless) {
            gameMode = new Endless(this);
        } else if (gameModeType == GameModeType.CombeDeCaron) {
            gameMode = new CombeDeCaron(this);
        }

        snowFall = new SnowFall(1, 2, width);
        snowFall.initializeSnowfall(width, height);

        snowSpray = new SnowSpray(3.0, 11.8);
        playerTracks = new Tracks(20, player.getRotation(), player.getSize());
    }

    //-- GETTERS --//

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Player getPlayer() {
        return player;
    }

    // Boolean getters use "is" prefix (JavaBeans convention)
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

    // Renamed getter to match camelCase field name
    public List<Finishline> getFinishLine() {
        return finishLine;
    }

    public String getResourcePack() {
        return RESOURCE_PACK;
    }

    public List<SnowParticle> getSnowParticles() {
        return snowFall.getSnowParticles();
    }

    public List<TrackParticle> getTrackParticles() {
        return playerTracks.getTrackParticles();
    }

    public List<SnowSpray.SprayParticle> getSprayParticles() {
        return snowSpray.getParticles();
    }

    public int getMargin() {
        return MARGIN;
    }

    public double getGameTime() {
        return gameTimer.getTime();
    }

    public String getFormattedGameTime() {
        return gameTimer.getFormattedTime();
    }

    public boolean hasFinishedRace() {
        return finishedRace;
    }

    // Used for alpine highscores
    public int getElapsedCentiseconds() {
        return (int) (gameTimer.getTime() * CENTISECONDS_PER_SECOND);   // magic number replaced
    }

    //-- SETTERS --//

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
            gameTimer.resume();
        }

        notifyObservers();
    }

    //-- WORLD UPDATE --//

    public void update() {
        if (isGameOver() || isGamePaused()) return;

        player.update();
        player.moveHorizontally(width, MARGIN);

        if (inputHandler != null) {
            if (inputHandler.isLeftPressed()) {
                player.rotate(Direction.LEFT);
            }
            if (inputHandler.isRightPressed()) {
                player.rotate(Direction.RIGHT);
            }
            if (inputHandler.isQuitPressed()) {
                System.exit(0);
            }
        }

        gameMode.update();

        handleCollision();
        removeOffScreenObjects();      // renamed from filterObstacles

        updateParticles();

        notifyObservers();
    }

    private void updateParticles() {
        snowFall.update((int) player.getCurrentSpeed());
        playerTracks.updateMovement((int) player.getYSpeed());

        double moveAngle = Math.atan2(player.getYSpeed(), player.getXSpeed());

        playerTracks.spawnTracks(player, moveAngle);

        if (player.getCurrentSpeed() > SPRAY_THRESHOLD_SPEED) {   // magic number replaced
            Point[] tips = player.getSkiTipPositions();

            for (Point tip : tips) {
                snowSpray.spawn(tip.x, tip.y, player.getCurrentSpeed(), moveAngle);
            }
        }

        snowSpray.update((int) player.getYSpeed());
    }

    private void handleCollision() {
        for (Obstacle obstacle : obstacles) {
            if (objectCollision.checkCollision(player, obstacle)) {
                setGameOver(true);
            }
        }
    }

    // Renamed method to better describe its purpose (removes off-screen objects)
    private void removeOffScreenObjects() {
        Iterator<Obstacle> itO = obstacles.iterator();
        while (itO.hasNext()) {
            Obstacle o = itO.next();
            o.update(player.getYSpeed());

            if (o.getPosition().y + o.getHeight() < 0) {
                itO.remove();
            }
        }

        Iterator<Gate> itG = gates.iterator();
        while (itG.hasNext()) {
            Gate g = itG.next();
            g.update(player.getYSpeed());

            if (g.getPosition().y + g.getHeight() < 0) {
                itG.remove();
            }
        }

        Iterator<Finishline> itF = finishLine.iterator();   // updated field name
        while (itF.hasNext()) {
            Finishline f = itF.next();
            f.update(player.getYSpeed());

            if (f.getPosition().y + f.getHeight() < 0) {
                itF.remove();
            }
        }
    }

    //-- OBSERVERS --//

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for (GameObserver observer : observers) {
            observer.worldUpdated();
        }
    }

    //-- GAME RESET --//

    public void restart(GameModeType gameModeType) {
        gameOver = false;
        gamePaused = false;
        newGameFlag = true;          // renamed field
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

        if (gameModeType == GameModeType.Endless) {
            gameMode = new Endless(this);
        } else if (gameModeType == GameModeType.CombeDeCaron) {
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
    public boolean pollNewGameFlag() {   // renamed from newGame()
        boolean wasNewGame = newGameFlag;
        newGameFlag = false;
        return wasNewGame;
    }

    public GameModeType getGameModeType() {
        return gameModeType;
    }
}