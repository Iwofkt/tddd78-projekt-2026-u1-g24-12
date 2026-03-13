package se.liu.simjolucul.dopeslope.effects.track;

import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.effects.ParticleConfig;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A square shaped particle that moves uppwards with the speed of the player
 */
public class TrackParticle extends Particle {
    private final int width;
    private final int height;
    private final int alpha;
    public TrackParticle(ParticleConfig config) {
        super(config);
        this.width = config.recWidthMin;
        this.height = config.recHeightMin;
        this.alpha = config.alphaMin;
    }

    @Override
    public void update(int speed) {
        y -= speed;   // world scrolls up
        life--;
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));

        AffineTransform old = g2d.getTransform();

        double centerX = x + width / 2.0;
        double centerY = y + height / 2.0;
        g2d.rotate(rotation, centerX, centerY);

        g2d.fillRect((int) x, (int) y, width, height);

        g2d.setTransform(old);
    }
}