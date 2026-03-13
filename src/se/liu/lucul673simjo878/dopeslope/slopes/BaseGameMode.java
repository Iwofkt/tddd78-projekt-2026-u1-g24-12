package se.liu.simjolucul.dopeslope.slopes;

import se.liu.simjolucul.dopeslope.game.GameBase;
import se.liu.simjolucul.dopeslope.gameObjects.*;
import se.liu.simjolucul.dopeslope.handlers.ImageLoader;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public abstract class BaseGameMode implements GameMode {
    // Common constants (can be overridden by subclasses if needed)
    protected static final int SPAWN_DISTANCE_THRESHOLD = 10;
    protected static final int OBSTACLE_TEXTURE_SCALE = 2;
    protected static final int GATE_TEXTURE_SCALE = 1;

    protected final Random rnd = new Random();

    protected final GameBase gameBase;
    protected final Player player;
    protected final List<Obstacle> obstacles;
    protected final List<Gate> gates;
    protected final List<Finishline> finishline; // maybe null if unused

    protected double deltaDistanceTraveled = 0;
    protected double oldPlayerDistance = 0;

    protected final BufferedImage treeImg;
    protected final BufferedImage gateLImg;
    protected final BufferedImage gateRImg;

    protected BaseGameMode(GameBase gameBase) {
	this.gameBase = gameBase;
	this.player = gameBase.getPlayer();
	this.obstacles = gameBase.getObstacles();
	this.gates = gameBase.getGates();
	this.finishline = gameBase.getFinishline();

	this.treeImg = ImageLoader.loadTextureSize(
		gameBase.getResourcePack(), "tree",
		OBSTACLE_TEXTURE_SCALE, OBSTACLE_TEXTURE_SCALE
	);
	this.gateLImg = ImageLoader.loadTexture(
		gameBase.getResourcePack(), "gateL"
	);
	this.gateRImg = ImageLoader.loadTexture(
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
    protected void spawnFinishLine() {}
}