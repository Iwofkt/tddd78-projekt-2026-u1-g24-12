package se.liu.simjolucul.dopeslope.slopes;

import se.liu.simjolucul.dopeslope.game.GameBase;
import se.liu.simjolucul.dopeslope.gameObjects.*;
import se.liu.simjolucul.dopeslope.handlers.GateSpawn;
import se.liu.simjolucul.dopeslope.handlers.collision.GateCollisionAlpine;
import se.liu.simjolucul.dopeslope.handlers.collision.GateCollisionEndless;

import java.util.List;

public class CombeDeCaron extends BaseGameMode {
    private static final int OBSTACLE_CHANCE_MAX = 30;
    private static final int OBSTACLE_CHANCE_THRESHOLD = 20;
    private static final int TREE_LEFT_X_MIN = -10;
    private static final int TREE_LEFT_X_MAX = 30;
    private static final int TREE_RIGHT_X_PADDING = 70;
    private static final int FINISH_LINE_DISTANCE = 4000;
    private static final int FINISH_LINE_X = 235;
    private static final int FINISH_LINE_WIDTH = 15;
    private static final int FINISH_LINE_HEIGHT = 50;

    private static final List<GateSpawn> GATE_SPAWNS = List.of(
            new GateSpawn(200, 100),
            new GateSpawn(450, 500),
            new GateSpawn(200, 1000),
            new GateSpawn(450, 1500),
            new GateSpawn(200, 2000),
            new GateSpawn(450, 2500),
            new GateSpawn(200, 3000),
            new GateSpawn(450, 3500)
    );

    private final GateCollisionAlpine gateCollisionAlpine = new GateCollisionAlpine();
    private final GateCollisionEndless gateCollisionEndless = new GateCollisionEndless();

    private int nextGateIndex = 0;
    private boolean finishLineSpawned = false;

    public CombeDeCaron(GameBase gameBase) {
        super(gameBase);
        // No extra images needed
    }

    @Override
    protected void spawnObstacles() {
        int obstacleChance = rnd.nextInt(OBSTACLE_CHANCE_MAX);
        if (obstacleChance >= OBSTACLE_CHANCE_THRESHOLD) {
            obstacles.add(new Tree(
                    rnd.nextInt(TREE_LEFT_X_MIN, TREE_LEFT_X_MAX),
                    gameBase.getHeight(), treeImg
            ));
            obstacles.add(new Tree(
                    rnd.nextInt(gameBase.getWidth() - TREE_RIGHT_X_PADDING, gameBase.getWidth()),
                    gameBase.getHeight(), treeImg
            ));
        }
    }

    @Override
    protected void spawnGates() {
        while (nextGateIndex < GATE_SPAWNS.size() &&
               player.getDistanceTraveled() >= GATE_SPAWNS.get(nextGateIndex).getDistance()) {
            GateSpawn gateSpawn = GATE_SPAWNS.get(nextGateIndex);
            gates.add(new Gate(
                    gateSpawn.getX(), gameBase.getHeight(),
                    gameBase.getWidth(), gateLImg, gateRImg
            ));
            nextGateIndex++;
        }
    }

    @Override
    protected void spawnFinishLine() {
        if (!finishLineSpawned && player.getDistanceTraveled() >= FINISH_LINE_DISTANCE) {
            finishline.add(new Finishline(
                    FINISH_LINE_X, gameBase.getHeight(),
                    FINISH_LINE_WIDTH, FINISH_LINE_HEIGHT,
                    gameBase.getResourcePack()
            ));
            finishLineSpawned = true;
        }
    }

    @Override
    protected void handleCollision() {
        for (Gate gate : gates) {
            if (gateCollisionAlpine.checkCollision(player, gate)) {
                gameBase.setGameOver(true);
            }
        }
        for (Finishline line : finishline) {
            if (gateCollisionEndless.checkCollision(player, line)) {
                gameBase.setFinishedRace(true);
                gameBase.setGameOver(true);
            }
        }
    }
}