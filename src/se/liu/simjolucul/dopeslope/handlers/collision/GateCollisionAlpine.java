package se.liu.simjolucul.dopeslope.handlers.collision;

import se.liu.simjolucul.dopeslope.gameobjects.Gate;
import se.liu.simjolucul.dopeslope.gameobjects.Obstacle;
import se.liu.simjolucul.dopeslope.gameobjects.Player;

public class GateCollisionAlpine implements CollisionHandler {
    public boolean checkCollision(Player player, Obstacle obstacle) {
        if (obstacle instanceof Gate gate) {
            if (gate.getLeftHitbox().intersects(player.getHitbox())) {
                return true;
            } else return gate.getRightHitbox().intersects(player.getHitbox());
        }
        return false;
    }
}
