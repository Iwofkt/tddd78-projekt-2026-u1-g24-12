package se.liu.simjolucul.dopeslope.handlers.collision;

import se.liu.simjolucul.dopeslope.gameObjects.Obstacle;
import se.liu.simjolucul.dopeslope.gameObjects.Player;

public class GateCollisionEndless implements CollisionHandler {
    public boolean checkCollision(Player player, Obstacle obstacle) {
        return obstacle.getHitbox().intersects(player.getHitbox());
    }
}
