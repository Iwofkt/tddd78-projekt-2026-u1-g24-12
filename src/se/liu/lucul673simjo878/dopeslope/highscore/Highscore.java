package se.liu.simjolucul.dopeslope.highscore;

public class Highscore {

    public static final int DNF_SCORE = Integer.MAX_VALUE;

    private String name = null;
    private int points;

    public Highscore() {
    }

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
}


