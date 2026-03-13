package se.liu.simjolucul.dopeslope.handlers;

/**
 * A simple game timer that measures elapsed time in seconds and milliseconds.
 * The timer can be started, paused, and reset.
 */
public class GameTimer {
    private long startTime;
    private double accumulated;
    private boolean running;

    private static final double NANOS_PER_SECOND = 1_000_000_000.0;

    public GameTimer() {
        reset();
    }

    /** Start or resume the timer. If already running, does nothing. */
    public void start() {
        if (!running) {
            startTime = System.nanoTime();
            running = true;
        }
    }

    /** Pause the timer. If already paused, does nothing. */
    public void pause() {
        if (running) {
            accumulated += (System.nanoTime() - startTime) / NANOS_PER_SECOND;
            running = false;
        }
    }

    /** Reset the timer to zero and stop it. */
    public void reset() {
        accumulated = 0.0;
        running = false;
    }

    /** Get current elapsed time in seconds. */
    public double getTime() {
        if (running) {
            return accumulated + (System.nanoTime() - startTime) / NANOS_PER_SECOND;
        } else {
            return accumulated;
        }
    }

    /** Get current elapsed time in milliseconds (rounded down). */
    public int getTimeInMillis() {
        return (int) (getTime() * 1000);
    }

    public static String formatMillis(int millis) {
        int minutes = millis / 60_000;
        int seconds = (millis / 1_000) % 60;
        int ms = millis % 1_000;
        return String.format("%02d:%02d.%03d", minutes, seconds, ms);
    }
}