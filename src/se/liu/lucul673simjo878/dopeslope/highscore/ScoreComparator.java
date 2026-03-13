package se.liu.simjolucul.dopeslope.highscore;

import java.util.Comparator;

public class ScoreComparator implements Comparator<Highscore> {

    // Jämför två Highscore-objekt baserat på poäng
    public int compare(Highscore o1, Highscore o2) {

        return Integer.compare(o2.getPoints(), o1.getPoints());
    }
}