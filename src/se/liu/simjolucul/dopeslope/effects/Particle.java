package se.liu.simjolucul.dopeslope.effects;

import java.awt.*;

/**
 * Base class for all visual particles in the game.
 * <p>
 * A particle has a position, size, lifetime, color, and optional velocity.
 * Subclasses must implement {@link #update(int)} to define behavior each frame.
 * The particle is considered alive as long as {@link #life} is positive.
 */
public abstract class Particle {
    protected double x;

    protected double y;

    protected double rotation;

    protected double velocityX;

    protected double velocityY;

    protected int size;

    protected int life;

    private final Color color;
    private final int alphaMin;
    private final int alphaMax;

    /**
     * Constructs a particle using the given configuration.
     * Position, rotation, size, lifetime, and color/alpha ranges are copied from the config.
     * Velocity fields are initially zero and may be set by subclasses.
     *
     * @param config configuration object containing all necessary parameters
     */
    protected Particle(ParticleConfig config) {
        this.x = config.x;
        this.y = config.y;
        this.rotation = config.angle;

        // Initialize velocity to zero (subclasses will set appropriate values)
        this.velocityX = 0.0;
        this.velocityY = 0.0;

        // Random size within allowed range
        size = config.radiusSizeMin + (int) (Math.random() * (config.radiusSizeMax - config.radiusSizeMin + 1));

        // Random lifetime within allowed range
        life = config.lifeMin + (int) (Math.random() * (config.lifeMax - config.lifeMin + 1));

        this.color = config.color;
        alphaMin = config.alphaMin;
        alphaMax = config.alphaMax;
    }

    /**
     * Updates the particle's state based on the game speed.
     * This method must be implemented by subclasses to define specific behavior
     * (e.g., movement, fading, spawning child particles).
     *
     * @param speed the current game speed, affecting how fast the particle moves
     */
    public abstract void update(int speed);

    /**
     * Checks whether the particle is still alive.
     *
     * @return {@code true} if lifetime is greater than zero, {@code false} otherwise
     */
    public boolean isAlive() {
        return life > 0;
    }

    /**
     * Draws the particle on the provided graphics context.
     * The particle is rendered as a filled oval with a random alpha value
     * within the configured range.
     *
     * @param g2d the graphics context to draw on
     */
    public void draw(Graphics2D g2d) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = alphaMin + (int) (Math.random() * (alphaMax - alphaMin + 1));

        g2d.setColor(new Color(r, g, b, alpha));
        g2d.fillOval((int) x, (int) y, size, size);
    }
}