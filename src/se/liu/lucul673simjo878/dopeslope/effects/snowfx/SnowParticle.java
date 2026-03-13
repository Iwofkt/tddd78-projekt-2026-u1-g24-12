package se.liu.simjolucul.dopeslope.effects.snowfx;


import se.liu.simjolucul.dopeslope.effects.Particle;
import se.liu.simjolucul.dopeslope.effects.ParticleConfig;

public class SnowParticle extends Particle {

    public SnowParticle(ParticleConfig config) {
        super(config);

        // velocity with spread
        vx = (Math.random() - 0.5) * config.spread + Math.sin(config.angle) * -config.speed * 0.2;
    }

    @Override
    public void update(int speed) {
        x += vx;
        y += speed;
        life--;
    }
}