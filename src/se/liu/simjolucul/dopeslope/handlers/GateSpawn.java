package se.liu.simjolucul.dopeslope.handlers;

/**
 * Used to effectively spawn Gates in predefined maps (not Endless)
 * @see se.liu.simjolucul.dopeslope.slopes.CombeDeCaron
 * @see se.liu.simjolucul.dopeslope.gameobjects.Gate
 */
public class GateSpawn {
    private final int x;
    private final int distance;

    public GateSpawn(int x, int distance) {
        this.x = x;
        this.distance = distance;
    }

    public int getX() {
        return x;
    }

    public int getDistance() {
        return distance;
    }
}
