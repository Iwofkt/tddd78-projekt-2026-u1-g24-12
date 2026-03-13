package se.liu.simjolucul.dopeslope.handlers.collision;

import se.liu.simjolucul.dopeslope.gameObjects.Obstacle;
import se.liu.simjolucul.dopeslope.gameObjects.Player;

public interface CollisionHandler {
    public boolean checkCollision(Player player, Obstacle obstacle);
}
