package se.liu.simjolucul.dopeslope.effects.track;

import se.liu.simjolucul.dopeslope.effects.ParticleConfig;
import se.liu.simjolucul.dopeslope.gameobjects.Player;
import java.awt.*;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates tracks behind the players
 * <p>
 * Track particles are generated behind both player skis
 * with a constant alpha and solid color so overlapping tracks aren't a visual problem
 */
public class Tracks {
    private final static int ALPHA_MAX = 255;
    private final static int ALPHA_MIN = 255;

    // ------ Track dimension constants ------
    private static final int TRACK_WIDTH = 6;
    private static final int TRACK_HEIGHT = 15;

    // ------ Track lifespan constants ------
    private static final int TRACK_LIFE_MIN = 100;
    private static final int TRACK_LIFE_MAX = 200;

    // ------ Track orientation constant (90 degrees = PI/2 radians) ------
    private static final double PERPENDICULAR_ANGLE = Math.PI / 2;

    private final ParticleConfig config;
    private final int spawnRate;
    private final List<TrackParticle> particles = new ArrayList<>();

    public Tracks(int spawnRate) {
        this.spawnRate = spawnRate;

        config = new ParticleConfig();
        config.position.setLocation(0, 0);
        config.rectWidth = ValueRange.of(TRACK_WIDTH, TRACK_WIDTH);
        config.rectHeight = ValueRange.of(TRACK_HEIGHT, TRACK_HEIGHT);
        config.life = ValueRange.of(TRACK_LIFE_MIN, TRACK_LIFE_MAX);
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

        //  ------ Orient particles perpendicular to movement direction ------
        config.angle = moveAngle + PERPENDICULAR_ANGLE;

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