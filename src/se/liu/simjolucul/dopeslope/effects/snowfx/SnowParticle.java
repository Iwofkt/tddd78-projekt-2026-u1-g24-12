package se.liu.simjolucul.dopeslope.effects.snowfx;

import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.effects.ParticleConfig;

public class SnowParticle extends Particle {
    private final double drift;   // horizontal movement per frame

    public SnowParticle(ParticleConfig config) {
        super(config);

        // Random horizontal drift between -spread/2 and +spread/2
        this.drift = (Math.random() - 0.5) * config.spread;
    }

    @Override
    public void update(int speed) {
        x += drift;
        y += speed;        // vertical fall tied to game speed
        life--;
    }
}