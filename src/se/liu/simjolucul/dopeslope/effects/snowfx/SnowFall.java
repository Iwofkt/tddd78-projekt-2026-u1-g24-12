package se.liu.simjolucul.dopeslope.effects.snowfx;

import se.liu.simjolucul.dopeslope.effects.ParticleConfig;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnowFall {
    private static final Random RND = new Random();

    private static final int INITIAL_PARTICLES = 250;
    private static final int SPREAD = 2;
    private static final int ALPHA_MAX = 140;
    private static final int ALPHA_MIN = 100;

    private ParticleConfig config;

    private int spawnRate;
    private int width, height; // store both dimensions for reset

    private final List<SnowParticle> snowParticles = new ArrayList<>();

    public SnowFall(int spawnRate, double angle, int width) {
        this.spawnRate = spawnRate;
        this.width = width;

        config = new ParticleConfig();
        config.y = 0;
        config.angle = angle;
        config.spread = SPREAD;
        config.radiusSizeMax = 8;
        config.radiusSizeMin = 4;
        config.lifeMax = 200;
        config.lifeMin = 100;
        config.alphaMax = ALPHA_MAX;
        config.alphaMin = ALPHA_MIN;
        config.color = Color.WHITE;
    }

    public List<SnowParticle> getSnowParticles() {
        return snowParticles;
    }

    public void update(int speed) {
        for (int i = 0; i < spawnRate; i++) {
            config.x = RND.nextInt(width);
            config.y = 0;
            snowParticles.add(new SnowParticle(config));
        }
        snowParticles.removeIf(p -> !p.isAlive());
        for (SnowParticle p : snowParticles) {
            p.update(speed);
        }
    }

    public void initializeSnowfall(int width, int height) {
        this.width = width;   // update in case width changed (though it shouldn't)
        this.height = height; // store for reset

        for (int i = 0; i < INITIAL_PARTICLES; i++) {
            config.x = RND.nextInt(width);
            config.y = RND.nextInt(height);
            snowParticles.add(new SnowParticle(config));
        }
    }

    public void reset() {
        snowParticles.clear();
        initializeSnowfall(width, height);
    }
}