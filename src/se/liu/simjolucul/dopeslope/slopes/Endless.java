package se.liu.simjolucul.dopeslope.slopes;

import se.liu.simjolucul.dopeslope.handlers.ImageLoader;
import se.liu.simjolucul.dopeslope.game.GameBase;
import se.liu.simjolucul.dopeslope.gameObjects.*;
import se.liu.simjolucul.dopeslope.handlers.collision.GateCollisionEndless;

import java.awt.image.BufferedImage;

public class Endless extends BaseGameMode {
    private static final int OBSTACLE_CHANCE_MAX = 30;
    private static final int GATE_CHANCE_MAX = 100;
    private static final int GATE_SPAWN_THRESHOLD = 0;
    private static final int GATE_MIN_X_OFFSET = 60;
    private static final int GATE_MAX_X_OFFSET = 170;

    private final BufferedImage rockImg;
    private final GateCollisionEndless gateCollisionEndless = new GateCollisionEndless();

    public Endless(GameBase gameBase) {
        super(gameBase);
        this.rockImg = ImageLoader.loadTextureSize(
                gameBase.getResourcePack(), "rock",
                OBSTACLE_TEXTURE_SCALE, OBSTACLE_TEXTURE_SCALE
        );
    }

    @Override
    protected void spawnObstacles() {
        int obstacleChance = rnd.nextInt(OBSTACLE_CHANCE_MAX);
        int margin = gameBase.getMargin();

        if (obstacleChance == 0) {
            int x = rnd.nextInt(margin, gameBase.getWidth() - margin);
            obstacles.add(new Rock(x, gameBase.getHeight(), rockImg));
        } else if (obstacleChance == 1) {
            int x = rnd.nextInt(margin, gameBase.getWidth() - margin);
            obstacles.add(new Tree(x, gameBase.getHeight(), treeImg));
        } else if (obstacleChance >= 20) {
            int leftX = rnd.nextInt(-treeImg.getWidth(), margin);
            obstacles.add(new Tree(leftX, gameBase.getHeight(), treeImg));
            int rightX = rnd.nextInt(
                    gameBase.getWidth() - margin - treeImg.getWidth(),
                    gameBase.getWidth()
            );
            obstacles.add(new Tree(rightX, gameBase.getHeight(), treeImg));
        }
    }

    @Override
    protected void spawnGates() {
        int gateChance = rnd.nextInt(GATE_CHANCE_MAX);
        if (gateChance == GATE_SPAWN_THRESHOLD) {
            int x = rnd.nextInt(GATE_MIN_X_OFFSET,
                                gameBase.getWidth() - GATE_MAX_X_OFFSET);
            gates.add(new Gate(x, gameBase.getHeight(),
                               gameBase.getWidth(), gateLImg, gateRImg));
        }
    }

    @Override
    protected void handleCollision() {
        for (Gate gate : gates) {
            if (gateCollisionEndless.checkCollision(player, gate)) {
                player.speedBoost();
            }
        }
    }
}