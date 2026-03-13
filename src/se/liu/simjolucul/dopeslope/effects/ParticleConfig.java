package se.liu.simjolucul.dopeslope.effects;

import java.awt.*;

/**
 * Configuration parameters for creating particles.
 * Different particle types use different subsets of these fields.
 */
public class ParticleConfig {
    // Position (used by all particles)
    public int x;
    public int y;

    // Movement direction – used by TrackParticle for rotation
    public double angle;

    // Randomness in horizontal movement – used by SnowParticle
    public double spread;

    // Size ranges for circular particles (Particle, SnowParticle, SprayParticle)
    public int radiusSizeMin;
    public int radiusSizeMax;

    // Size ranges for rectangular particles (TrackParticle)
    public int recHeightMin;
    public int recHeightMax;
    public int recWidthMin;
    public int recWidthMax;

    // Lifespan range (all particles)
    public int lifeMin;
    public int lifeMax;

    // Color and transparency (all particles)
    public Color color;
    public int alphaMin;
    public int alphaMax;

    public ParticleConfig() {
        // Reasonable defaults
        this.x = 10;
        this.y = 10;
        this.angle = 0;
        this.spread = 6;
        this.radiusSizeMin = 20;
        this.radiusSizeMax = 40;
        this.recHeightMin = 1;
        this.recHeightMax = 20;
        this.recWidthMin = 1;
        this.recWidthMax = 20;
        this.lifeMin = 10;
        this.lifeMax = 30;
        this.color = Color.WHITE;
        this.alphaMin = 90;
        this.alphaMax = 180;
    }

    // Copy constructor
    public ParticleConfig(ParticleConfig other) {
        this.x = other.x;
        this.y = other.y;
        this.angle = other.angle;
        this.spread = other.spread;
        this.radiusSizeMin = other.radiusSizeMin;
        this.radiusSizeMax = other.radiusSizeMax;
        this.recHeightMin = other.recHeightMin;
        this.recHeightMax = other.recHeightMax;
        this.recWidthMin = other.recWidthMin;
        this.recWidthMax = other.recWidthMax;
        this.lifeMin = other.lifeMin;
        this.lifeMax = other.lifeMax;
        this.color = other.color;
        this.alphaMin = other.alphaMin;
        this.alphaMax = other.alphaMax;
    }
}