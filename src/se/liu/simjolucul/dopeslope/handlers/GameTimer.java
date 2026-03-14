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
    private static final int MILLIS_PER_SECOND = 1000;
    private static final int SEC_PER_MINUTE = 60;

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

    public void pause() {
        if (running) {
            accumulated += (System.nanoTime() - startTime) / NANOS_PER_SECOND;
            running = false;
        }
    }

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
        return (int) (getTime() * MILLIS_PER_SECOND);
    }

    public static String formatMillis(int millis) {
        int totalSeconds = millis / MILLIS_PER_SECOND;
        int minutes = totalSeconds / SEC_PER_MINUTE;
        int seconds = totalSeconds % SEC_PER_MINUTE;
        int ms = millis % MILLIS_PER_SECOND;
        return String.format("%02d:%02d.%03d", minutes, seconds, ms);
    }
}