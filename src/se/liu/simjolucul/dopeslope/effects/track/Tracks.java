package se.liu.simjolucul.dopeslope.effects.track;

import se.liu.simjolucul.dopeslope.effects.ParticleConfig;
import se.liu.simjolucul.dopeslope.gameobjects.Player;
import java.awt.*;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates tracks behund the players
 * <p>
 * Track particles are genreated behind both player skis
 * with a constand alpha and solid color so overlapping tracks are'nt a visual problem
 */
public class Tracks {
    private final static int ALPHA_MAX = 255;
    private final static int ALPHA_MIN = 255;

    private final ParticleConfig config;
    private final int spawnRate;
    private final List<TrackParticle> particles = new ArrayList<>();

    public Tracks(int spawnRate) {
        this.spawnRate = spawnRate;

        config = new ParticleConfig();
        config.position.setLocation(0,0);
        config.rectWidth = ValueRange.of(6, 6);
        config.rectHeight = ValueRange.of(15, 15);
        config.life = ValueRange.of(100, 200);
        config.alpha = ValueRange.of(ALPHA_MIN, ALPHA_MAX);
        config.color = Color.GRAY;
    }

    public List<TrackParticle> getParticles() {
        return particles;
    }

    public void update(int speed) {
        particles.removeIf(p -> !p.isAlive());
        for (TrackParticle p : particles) {
            p.update(speed);
        }
    }

    public void spawnTracks(Player player, double moveAngle) {
        Point[] tips = player.getSkiTipPositions();
        Point leftTip = tips[0];
        Point rightTip = tips[1];

        // Orient particles perpendicular to movement direction
        config.angle = moveAngle + Math.PI / 2;

        int maxWidth = (int) config.rectWidth.getMaximum();
        int maxHeight = (int) config.rectHeight.getMaximum();
        int halfWidth = maxWidth / 2;
        int halfHeight = maxHeight / 2;

        for (int i = 0; i < spawnRate; i++) {
            // Left ski
            config.position.setLocation(leftTip.x - halfWidth, leftTip.y - halfHeight);
            particles.add(new TrackParticle(config));

            // Right ski
            config.position.setLocation(rightTip.x - halfWidth, rightTip.y - halfHeight);
            particles.add(new TrackParticle(config));
        }
    }

    public void clear() {
        particles.clear();
    }
}