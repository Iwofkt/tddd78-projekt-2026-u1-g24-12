package se.liu.simjolucul.dopeslope.effects;

import java.awt.*;

public abstract class Particle {

    public double x, y;
    protected double rotation;
    public double vx, vy;
    public int size;
    public int life;
    private final Color color;
    private final int alphaMin;
    private final int alphaMax;

    public Particle(ParticleConfig config) {

        this.x = config.x;
        this.y = config.y;
        this.rotation = config.angle;

        // size
        size = config.radiusSizeMin + (int)(Math.random() * (config.radiusSizeMax - config.radiusSizeMin + 1));

        // life
        life = config.lifeMin + (int)(Math.random() * (config.lifeMax - config.lifeMin + 1));

        // color
        this.color = config.color;
        alphaMin = config.alphaMin;
        alphaMax = config.alphaMax;
    }

    public void update() {
        x += vx;
        y += vy;
        life--;
    }
    public abstract void update(int speed);

    public boolean isAlive() {
        return life > 0;
    }

    public void draw(Graphics2D g2d) {
        int r = this.color.getRed();
        int g = this.color.getGreen();
        int b = this.color.getBlue();
        int alpha = alphaMin + (int)(Math.random() * (alphaMax - alphaMin + 1));

        g2d.setColor(new Color(r, g, b, alpha));
        g2d.fillOval((int)x, (int)y, size, size);
    }
}
