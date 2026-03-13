package se.liu.simjolucul.dopeslope.effects.snowfx;

import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.effects.ParticleConfig;

/**
 * A snowflake particle that falls downward with a random horizontal drift.
 * <p>
 * The particle's horizontal movement is determined by {@link #drift},
 * which is a random value between -spread/2 and +spread/2 taken from the config.
 */
public class SnowParticle extends Particle {

    /** Horizontal movement per frame. */
    private final double drift;

    /** Factor used to center the random drift range around zero. */
    private static final double RANGE_CENTER = 0.5;


    public SnowParticle(ParticleConfig config) {
        super(config);
        // Random horizontal drift between -spread/2 and +spread/2
        this.drift = (Math.random() - RANGE_CENTER) * config.spread;
    }

    @Override
    public void update(int speed) {
        x += drift;
        y += speed;
        life--;
    }
}