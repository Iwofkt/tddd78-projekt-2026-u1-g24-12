package se.liu.simjolucul.dopeslope.effects.track;

import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.effects.ParticleConfig;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class TrackParticle extends Particle {
    private final ParticleConfig config;
    private final Random RND = new Random();
    public TrackParticle(ParticleConfig config) {
        super(config);
        this.config = new ParticleConfig(config);
    }

    @Override
    public void update(int speed) {
        y -= speed;
        life--;
    }

    @Override
    public void draw(Graphics2D g2d) {
        int r = config.color.getRed();
        int g = config.color.getGreen();
        int b = config.color.getBlue();
        int alpha = RND.nextInt(config.alphaMin, config.alphaMax + 1);


        g2d.setColor(new Color(r, g, b, alpha));

        AffineTransform old = g2d.getTransform();

        g2d.rotate(rotation, x + config.recWidthMax / 2.0, y + config.recHeightMax / 2.0);

        g2d.fillRect((int)x, (int)y, config.recWidthMax, config.recHeightMax + 1);

        g2d.setTransform(old);
    }
}
