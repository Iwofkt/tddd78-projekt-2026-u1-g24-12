package se.liu.simjolucul.dopeslope.handlers.collision;

import se.liu.simjolucul.dopeslope.gameobjects.Obstacle;
import se.liu.simjolucul.dopeslope.gameobjects.Player;

public interface CollisionHandler {
    public boolean checkCollision(Player player, Obstacle obstacle);
}
