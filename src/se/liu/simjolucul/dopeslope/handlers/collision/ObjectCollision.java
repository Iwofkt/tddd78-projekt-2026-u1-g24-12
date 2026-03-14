package se.liu.simjolucul.dopeslope.handlers.collision;

import se.liu.simjolucul.dopeslope.gameobjects.Obstacle;
import se.liu.simjolucul.dopeslope.gameobjects.Player;

/**
 * Handles the collision detection between a player and any obstacle
 * with one hitbox
 */
public class ObjectCollision implements CollisionHandler {
    public boolean isCollisionDetected(Player player, Obstacle obstacle) {
        return obstacle.getHitbox().intersects(player.getHitbox());
    }
}
