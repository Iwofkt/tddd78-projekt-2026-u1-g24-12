package se.liu.simjolucul.dopeslope.effects;

import java.awt.*;

public class ParticleConfig {
    public int  x;
    public int y;

    public double speed;        // base speed of particle
    public double angle;        // direction of movement
    public double spread;       // randomness in direction

    public int radiusSizeMin;         // min size
    public int radiusSizeMax;         // max size

    public int recHeightMax;
    public int recHeightMin;
    public int recWidthMax;
    public int recWidthMin;

    public int lifeMin;         // min lifespan
    public int lifeMax;         // max lifespan

    public Color color;         // base color
    public int alphaMin;        // transparency range
    public int alphaMax;

    public ParticleConfig() {
        // default values
        this.x = 10;
        this.y = 10;

        this.speed = 0;
        this.angle = 0;
        this.spread = 6;

        this.radiusSizeMin = 20;
        this.radiusSizeMax = 40;

        recHeightMax = 20;
        recHeightMin = 1;
        recWidthMax = 20;
        recWidthMin = 1;

        this.lifeMin = 10;
        this.lifeMax = 30;
        this.color = java.awt.Color.WHITE;
        this.alphaMin = 90;
        this.alphaMax = 180;
    }

    //Copier
    public ParticleConfig(ParticleConfig other) {
        this.x = other.x;
        this.y = other.y;
        this.speed = other.speed;
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