package se.liu.simjolucul.dopeslope.handlers.collision;

import se.liu.simjolucul.dopeslope.gameobjects.Obstacle;
import se.liu.simjolucul.dopeslope.gameobjects.Player;

/**
 * Handles the collision detection between a player and a gate obstacle
 * in the endless gameode
 */
public class GateCollisionEndless implements CollisionHandler {
    public boolean isCollisionDetected(Player player, Obstacle obstacle) {
        return obstacle.getHitbox().intersects(player.getHitbox());
    }
}
