package se.liu.simjolucul.dopeslope.effects.snowfx;

import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.effects.ParticleConfig;
import java.awt.*;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages a continuous snowfall effect covering the entire screen.
 * <p>
 * Snowflakes are generated at the top of the screen at a fixed rate per frame,
 * Each snowflake falls downward with random horizontal drift
 */
public class SnowFall {
    private static final Random RND = new Random();

    private static final int INITIAL_PARTICLES = 250;
    private static final int SPREAD = 2;
    private static final int ALPHA_MAX = 140;
    private static final int ALPHA_MIN = 100;

    //  ------ Snowflake size defaults ------
    private static final int SNOWFLAKE_SIZE_MIN = 4;
    private static final int SNOWFLAKE_SIZE_MAX = 8;

    // ------ Snowflake lifespan defaults ------
    private static final int SNOWFLAKE_LIFE_MIN = 100;
    private static final int SNOWFLAKE_LIFE_MAX = 200;

    private final ParticleConfig config;
    private final int spawnRate;
    private int width, height;

    private final List<Particle> particles = new ArrayList<>();

    public SnowFall(int spawnRate, int width) {
        this.spawnRate = spawnRate;
        this.width = width;

        config = new ParticleConfig();
        config.position = new Point(0, 0);
        config.spread = SPREAD;
        config.radiusSize = ValueRange.of(SNOWFLAKE_SIZE_MIN, SNOWFLAKE_SIZE_MAX);
        config.life = ValueRange.of(SNOWFLAKE_LIFE_MIN, SNOWFLAKE_LIFE_MAX);
        config.alpha = ValueRange.of(ALPHA_MIN, ALPHA_MAX);
        config.color = Color.WHITE;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void update(int speed) {
        // Spawn new snowflakes at the top
        for (int i = 0; i < spawnRate; i++) {
            config.position.setLocation(RND.nextInt(width), 0);
            particles.add(new SnowParticle(config));
        }
        // Remove dead particles and update the rest
        particles.removeIf(p -> !p.isAlive());
        for (Particle p : particles) {
            p.update(speed);
        }
    }

    public void initializeSnowfall(int width, int height) {
        this.width = width;
        this.height = height;

        for (int i = 0; i < INITIAL_PARTICLES; i++) {
            config.position.setLocation(RND.nextInt(width), RND.nextInt(height));
            particles.add(new SnowParticle(config));
        }
    }

    public void reset() {
        particles.clear();
        initializeSnowfall(width, height);
    }
}