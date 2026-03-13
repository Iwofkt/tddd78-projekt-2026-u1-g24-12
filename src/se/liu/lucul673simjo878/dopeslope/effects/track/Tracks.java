package se.liu.simjolucul.dopeslope.effects.track;

import se.liu.simjolucul.dopeslope.effects.ParticleConfig;
import se.liu.simjolucul.dopeslope.gameObjects.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tracks {
    private static final Random RND = new Random();

    private final static int ALPHA_MAX = 255;
    private final static int ALPHA_MIN = 255;

    private final ParticleConfig config;
    private final int spawnrate;
    private final List<TrackParticle> TrackParticles = new ArrayList<>();

    public Tracks(int spawnrate, double angle, int width) {
        this.spawnrate = spawnrate;

        config = new ParticleConfig();
        config.y = 0;
        config.angle = angle;
        config.recWidthMin = 6;
        config.recWidthMax = 6;
        config.recHeightMin = 15;
        config.recHeightMax = 15;
        config.lifeMax = 200;
        config.lifeMin = 100;
        config.alphaMax = ALPHA_MAX;
        config.alphaMin = ALPHA_MIN;
        config.color = Color.GRAY;
    }

    public List<TrackParticle> getTrackParticles() {
        return TrackParticles;
    }

    public void updateMovement(int speed) {
        TrackParticles.removeIf(p -> !p.isAlive());
        for (TrackParticle p : TrackParticles) {
            p.update(speed);
        }
    }
    public void spawnTracks(Player player, double moveAngle) {
        Point[] tips = player.getSkiTipPositions();
        Point leftTip = tips[0];
        Point rightTip = tips[1];

        // Orient particles along the movement direction
        config.angle = moveAngle + Math.PI / 2;

        for (int i = 0; i < spawnrate; i++) {
            // Left ski
            config.x = leftTip.x - config.recWidthMax / 2;
            config.y = leftTip.y - config.recHeightMax / 2;
            TrackParticles.add(new TrackParticle(config));

            // Right ski
            config.x = rightTip.x - config.recWidthMax / 2;
            config.y = rightTip.y - config.recHeightMax / 2;
            TrackParticles.add(new TrackParticle(config));
        }
    }

    public void clear() {
        TrackParticles.clear();
    }
}