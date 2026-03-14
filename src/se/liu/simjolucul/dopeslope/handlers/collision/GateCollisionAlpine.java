package se.liu.simjolucul.dopeslope.handlers.collision;

import se.liu.simjolucul.dopeslope.gameobjects.Gate;
import se.liu.simjolucul.dopeslope.gameobjects.Obstacle;
import se.liu.simjolucul.dopeslope.gameobjects.Player;


/**
 * Handles the collision detection between a player and a gate obstacle
 * in alpine gamemodes (CombeDeCaron)
 */
public class GateCollisionAlpine implements CollisionHandler
{


    @Override

    public boolean isCollisionDetected(final Player player, final Obstacle obstacle) {

        return obstacle instanceof Gate gate &&
               (gate.getLeftHitbox().intersects(player.getHitbox()) ||
                gate.getRightHitbox().intersects(player.getHitbox()));
    }
}
