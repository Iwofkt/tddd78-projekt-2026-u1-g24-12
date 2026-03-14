package se.liu.simjolucul.dopeslope.highscore;

/**
 * Represents a highscore with a player's name and points.
 */
public class Highscore {

    private static final int DNF_SCORE = Integer.MAX_VALUE;

    private String name;
    private int points;

    public Highscore(String name, int points) {
        this.name = name;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public boolean isDNF() {
        return points == DNF_SCORE;
    }

    public static int getDnfScore() {
        return DNF_SCORE;
    }
}


