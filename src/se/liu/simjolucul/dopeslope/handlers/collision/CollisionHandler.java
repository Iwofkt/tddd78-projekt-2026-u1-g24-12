package se.liu.simjolucul.dopeslope.handlers.collision;

import se.liu.simjolucul.dopeslope.gameobjects.Obstacle;
import se.liu.simjolucul.dopeslope.gameobjects.Player;

/**
 * Interface responsible for handling collision detection between player and obstacles.
 */
public interface CollisionHandler {

    /**
     * Determines if a collision between the player and the obstacle has occurred.
     *
     * @param player The player object involved in the collision.
     * @param obstacle The obstacle object involved in the collision.
     * @return true if the player and obstacle collided, false otherwise.
     */
    boolean isCollisionDetected(Player player, Obstacle obstacle);
}
