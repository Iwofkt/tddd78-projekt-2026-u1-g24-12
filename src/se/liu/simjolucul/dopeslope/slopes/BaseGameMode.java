package se.liu.simjolucul.dopeslope.slopes;

import se.liu.simjolucul.dopeslope.game.GameBase;
import se.liu.simjolucul.dopeslope.gameobjects.*;
import se.liu.simjolucul.dopeslope.handlers.ImageLoader;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public abstract class BaseGameMode implements GameMode {
    // Common constants

    protected static final int SPAWN_DISTANCE_THRESHOLD = 10;
    protected static final int OBSTACLE_TEXTURE_SCALE = 2;

    protected final static Random RND = new Random();

    protected final GameBase gameBase;
    protected final Player player;
    protected final List<Obstacle> obstacles;
    protected final List<Gate> gates;
    protected final List<Finishline> finishline;

    protected double deltaDistanceTraveled = 0;
    protected double oldPlayerDistance = 0;

    protected final BufferedImage treeImg;
    protected final BufferedImage leftGateImg;
    protected final BufferedImage rightGateImg;

    protected BaseGameMode(GameBase gameBase) {
	this.gameBase = gameBase;
	this.player = gameBase.getPlayer();
	this.obstacles = gameBase.getObstacles();
	this.gates = gameBase.getGates();
	this.finishline = gameBase.getFinishLine();

	this.treeImg = ImageLoader.loadTextureSize(
		gameBase.getResourcePack(), "tree",
		OBSTACLE_TEXTURE_SCALE, OBSTACLE_TEXTURE_SCALE
	);
	this.leftGateImg = ImageLoader.loadTexture(
		gameBase.getResourcePack(), "gateL"
	);
	this.rightGateImg = ImageLoader.loadTexture(
		gameBase.getResourcePack(), "gateR"
	);
    }

    @Override
    public void update() {
	handleCollision();

	deltaDistanceTraveled = player.getDistanceTraveled() - oldPlayerDistance;
	if (deltaDistanceTraveled > SPAWN_DISTANCE_THRESHOLD) {
	    oldPlayerDistance = player.getDistanceTraveled();
	    spawnObstacles();
	    spawnGates();
	    spawnFinishLine();
	}
    }

    protected abstract void handleCollision();
    protected abstract void spawnObstacles();
    protected abstract void spawnGates();

    // Default no-op for modes without finish line
    abstract void spawnFinishLine();
}