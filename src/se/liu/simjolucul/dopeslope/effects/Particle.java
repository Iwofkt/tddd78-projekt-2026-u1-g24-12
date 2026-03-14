package se.liu.simjolucul.dopeslope.effects;

import java.awt.*;

/**
 * Base class for all visual particles in the game.
 * <p>
 * A particle has a position, rotation, size, lifetime, color, and alpha range.
 * Subclasses must implement {@link #update(int)} to define behavior each frame.
 * The particle is considered alive as long as {@link #life} is positive.
 */
public abstract class Particle {
    protected double x;
    protected double y;
    protected double rotation;      // used by TrackParticle for orientation
    protected int size;
    protected int life;

    protected final Color color;
    private final int alphaMin;
    private final int alphaMax;

    /**
     * Constructs a particle using the given configuration.
     * Position, rotation, size, lifetime, and color/alpha ranges are copied from the config.
     *
     * @param config configuration object containing all necessary parameters
     */
    protected Particle(ParticleConfig config) {
        this.x = config.position.getX();
        this.y = config.position.getY();
        this.rotation = config.angle;

        // Random size within range
        size = (int) config.radiusSize.getMinimum() +
               (int) (Math.random() * ((int) config.radiusSize.getMaximum() -
                                       (int) config.radiusSize.getMinimum() + 1));

        // Random lifetime within range
        life = (int) config.life.getMinimum() +
               (int) (Math.random() * ((int) config.life.getMaximum() -
                                       (int) config.life.getMinimum() + 1));

        this.color = config.color;
        this.alphaMin = (int) config.alpha.getMinimum();
        this.alphaMax = (int) config.alpha.getMaximum();
    }

    /**
     * Updates the particle's state based on the game speed.
     *
     * @param speed the current game speed, affecting how fast the particle moves
     */
    public abstract void update(int speed);

    /**
     * Checks whether the particle is still alive.
     */
    public boolean isAlive() {
        return life > 0;
    }

    /**
     * Draws the particle on the provided graphics context.
     * The particle is rendered as a filled oval with a random alpha value
     * within the configured range.
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