package se.liu.simjolucul.dopeslope.handlers;

public class GameTimer {
    private long startTime;      // nanoseconds when timer started or resumed
    private long pauseTime;      // nanoseconds when timer was paused
    private double accumulated;  // total seconds accumulated before pause
    private boolean running;

    public GameTimer() {
        reset();
    }

    /** Start or resume the timer. */
    public void start() {
        if (!running) {
            startTime = System.nanoTime();
            running = true;
        }
    }

    /** Pause the timer. */
    public void pause() {
        if (running) {
            accumulated += (System.nanoTime() - startTime) / 1_000_000_000.0;
            running = false;
        }
    }

    /** Resume after pause. */
    public void resume() {
        if (!running) {
            startTime = System.nanoTime();
            running = true;
        }
    }

    /** Reset to zero. */
    public void reset() {
        accumulated = 0.0;
        running = false;
    }

    /** Get current elapsed time in seconds. */
    public double getTime() {
        if (running) {
            return accumulated + (System.nanoTime() - startTime) / 1_000_000_000.0;
        } else {
            return accumulated;
        }
    }

    /** Get formatted time mm:ss.ms (or hh:mm:ss if needed). */
    public String getFormattedTime() {
        double total = getTime();
        int minutes = (int) (total / 60);
        int seconds = (int) (total % 60);
        int millis = (int) ((total - (int) total) * 1000);
        return String.format("%02d:%02d.%03d", minutes, seconds, millis);
    }
}