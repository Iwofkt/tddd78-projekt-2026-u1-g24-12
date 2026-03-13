package se.liu.simjolucul.dopeslope.effects.snowfx;

import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.effects.ParticleConfig;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnowSpray {
    private static final Random RND = new Random();

    // Constants for magic numbers
    private static final double OFFSET_STD_DEV = 3.0;
    private static final double SPREAD_ANGLE = 0.5;
    private static final double SPEED_FACTOR_MIN = 0.5;
    private static final double SPEED_FACTOR_RANGE = 0.5;

    private final List<SprayParticle> particles = new ArrayList<>();
    private final double spawnRate;          // particles per spawn call
    private final double speedThreshold;      // minimum speed to spawn
    private final ParticleConfig baseConfig;

    public SnowSpray(double spawnRate, double speedThreshold) {
        this.spawnRate = spawnRate;
        this.speedThreshold = speedThreshold;

        baseConfig = new ParticleConfig();
        baseConfig.radiusSizeMin = 2;
        baseConfig.radiusSizeMax = 5;
        baseConfig.lifeMin = 20;
        baseConfig.lifeMax = 40;
        baseConfig.alphaMin = 150;
        baseConfig.alphaMax = 255;
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
            double offsetX = RND.nextGaussian() * OFFSET_STD_DEV;
            double offsetY = RND.nextGaussian() * OFFSET_STD_DEV;

            double baseAngle = directionAngle + Math.PI; // opposite direction

            // Angle with spread: center around baseAngle, random within [-SPREAD_ANGLE/2, +SPREAD_ANGLE/2]
            double angle = baseAngle + (RND.nextDouble() - 0.5) * SPREAD_ANGLE;

            double speed = playerSpeed * (SPEED_FACTOR_MIN + RND.nextDouble() * SPEED_FACTOR_RANGE);

            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;

            // Create a copy of the base config to avoid shared state
            ParticleConfig config = new ParticleConfig(baseConfig);
            config.x = (int) (x + offsetX);
            config.y = (int) (y + offsetY);

            particles.add(new SprayParticle(config, vx, vy));
        }
    }

    public static class SprayParticle extends Particle {
        // Renamed to avoid short names and potential hiding of superclass fields
        private double velocityX, velocityY;

        private final Color color;
        private final int alphaMin, alphaMax;
        private final int maxLife; // store initial life for fading

        public SprayParticle(ParticleConfig config, double vx, double vy) {
            super(config);
            this.velocityX = vx;
            this.velocityY = vy;
            this.color = config.color;
            this.alphaMin = config.alphaMin;
            this.alphaMax = config.alphaMax;
            this.maxLife = this.life; // capture the randomized life from super
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

            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            g2d.fillOval((int) x, (int) y, size, size);
        }
    }

    public void clear() {
        particles.clear();
    }
}