package se.liu.simjolucul.dopeslope.handlers;

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
