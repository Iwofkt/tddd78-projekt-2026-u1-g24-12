package se.liu.simjolucul.dopeslope.effects.snowfx;

import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.effects.ParticleConfig;

import java.awt.*;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages a spray of snow particles that trails behind the player at high speeds.
 * <p>
 * When the player's speed exceeds {@link #speedThreshold}, particles are spawned
 * near the player's position with random offsets and velocities, creating a
 * snow‑spray effect.
 */
public class SnowSpray {
    private static final Random RND = new Random();

    private static final double OFFSET_STANDARD_DEV = 3.0;
    private static final double SPREAD_ANGLE = 0.5;
    private static final double SPEED_FACTOR_MIN = 0.5;
    private static final double SPEED_FACTOR_RANGE = 0.5;

    // ------ Spray particle size defaults ------
    private static final int SPRAY_SIZE_MIN = 2;
    private static final int SPRAY_SIZE_MAX = 5;

    // ------ Spray particle lifespan defaults ------
    private static final int SPRAY_LIFE_MIN = 20;
    private static final int SPRAY_LIFE_MAX = 40;

    // ------ Spray particle alpha transparency defaults ------
    private static final int SPRAY_ALPHA_MIN = 150;
    private static final int SPRAY_ALPHA_MAX = 255;

    private final List<SprayParticle> particles = new ArrayList<>();
    /** particles per spawn call */
    private final double spawnRate;
    /** minimum speed to spawn*/
    private final double speedThreshold;
    private final ParticleConfig baseConfig;

    public SnowSpray(double spawnRate, double speedThreshold) {
        this.spawnRate = spawnRate;
        this.speedThreshold = speedThreshold;

        baseConfig = new ParticleConfig();
        baseConfig.radiusSize = ValueRange.of(SPRAY_SIZE_MIN, SPRAY_SIZE_MAX);
        baseConfig.life = ValueRange.of(SPRAY_LIFE_MIN, SPRAY_LIFE_MAX);
        baseConfig.alpha = ValueRange.of(SPRAY_ALPHA_MIN, SPRAY_ALPHA_MAX);
        baseConfig.color = Color.WHITE;
    }

    public List<SprayParticle> getParticles() {
        return particles;
    }

    public void update(int worldSpeed) {
        particles.removeIf(p -> !p.isAlive());
        for (SprayParticle p : particles) {
            p.update(worldSpeed);
        }
    }

    public void spawn(double x, double y, double playerSpeed, double directionAngle) {
        if (playerSpeed < speedThreshold) return;

        int count = (int) Math.floor(spawnRate);
        double remainder = spawnRate - count;
        if (RND.nextDouble() < remainder) count++;

        for (int i = 0; i < count; i++) {
            double gaussianOffset = RND.nextGaussian() * OFFSET_STANDARD_DEV;
            double offsetX = gaussianOffset;
            double offsetY = gaussianOffset;

            double baseAngle = directionAngle + Math.PI; // opposite direction

            /** Angle with spread: center around baseAngle, random within [-SPREAD_ANGLE/2, +SPREAD_ANGLE/2]*/
            double angle = baseAngle + (RND.nextDouble() - SPREAD_ANGLE) * SPREAD_ANGLE;

            double speed = playerSpeed * (SPEED_FACTOR_MIN + RND.nextDouble() * SPEED_FACTOR_RANGE);

            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;

            ParticleConfig config = new ParticleConfig(baseConfig);
            config.position.setLocation((int) (x + offsetX), (int) (y + offsetY));
            particles.add(new SprayParticle(config, vx, vy));
        }
    }

    public static class SprayParticle extends Particle {
        private double velocityX, velocityY;

        private final Color sprayColor;
        private final int alphaMin, alphaMax;

        /** store initial life for fading */
        private final int maxLife;

        public SprayParticle(ParticleConfig config, double vx, double vy) {
            super(config);
            this.velocityX = vx;
            this.velocityY = vy;
            this.sprayColor = config.color;
            this.alphaMin = (int) config.alpha.getMinimum();
            this.alphaMax = (int) config.alpha.getMaximum();
            this.maxLife = this.life;
        }

        @Override
        public void update(int worldSpeed) {
            y -= worldSpeed; // world scrolls up
            x += velocityX;
            y += velocityY;
            life--;
        }

        @Override
        public void draw(Graphics2D g2d) {
            float lifeRatio = (float) life / maxLife;
            int alpha = (int) (alphaMin + (alphaMax - alphaMin) * lifeRatio);
            alpha = Math.max(alphaMin, Math.min(alphaMax, alpha)); // clamp

            g2d.setColor(new Color(sprayColor.getRed(), sprayColor.getGreen(), sprayColor.getBlue(), alpha));
            g2d.fillOval((int) x, (int) y, size, size);
        }
    }

    public void clear() {
        particles.clear();
    }
}