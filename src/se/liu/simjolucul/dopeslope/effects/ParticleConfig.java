package se.liu.simjolucul.dopeslope.effects;

import java.awt.*;

/**
 * Configuration parameters for creating particles.
 * Different particle types use different subsets of these fields.
 */
public class ParticleConfig {

    /** X-coordinate of the particle's starting position */
    public int x;

    /** Y-coordinate of the particle's starting position */
    public int y;

    /** Direction angle in radians */
    public double angle;

    /** Randomness in horizontal movement */
    public double spread;

    /** Minimum size for circular particles*/
    public int radiusSizeMin;
    /** Maximum size for circular particles. */
    public int radiusSizeMax;

    /** Minimum height for rectangular particles */
    public int recHeightMin;
    /** Maximum height for rectangular particles. */
    public int recHeightMax;
    /** Minimum width for rectangular particles. */
    public int recWidthMin;
    /** Maximum width for rectangular particles. */
    public int recWidthMax;

    /** Minimum lifespan in frames */
    public int lifeMin;
    /** Maximum lifespan in frames. */
    public int lifeMax;

    /** Base color of particles */
    public Color color = null;
    /** Minimum transparency value. */
    public int alphaMin;
    /** Maximum alpha value. */
    public int alphaMax;

    /** Creates a config with reasonable default values. */
    public ParticleConfig() {
        // ... (constructor body unchanged)
    }

    /** Copy constructor. */
    public ParticleConfig(ParticleConfig other) {
        this.x = other.x;
        this.y = other.y;
        this.angle = other.angle;
        this.spread = other.spread;
        this.radiusSizeMin = other.radiusSizeMin;
        this.radiusSizeMax = other.radiusSizeMax;
        this.recHeightMax = other.recHeightMax;
        this.recHeightMin = other.recHeightMin;
        this.recWidthMax = other.recWidthMax;
        this.recWidthMin = other.recWidthMin;
        this.lifeMin = other.lifeMin;
        this.lifeMax = other.lifeMax;
        this.color = other.color;
        this.alphaMin = other.alphaMin;
        this.alphaMax = other.alphaMax;
    }
}