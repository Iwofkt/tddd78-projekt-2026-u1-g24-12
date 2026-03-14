package se.liu.simjolucul.dopeslope.effects;

import java.awt.Color;
import java.awt.Point;
import java.time.temporal.ValueRange;

/**
 * Configuration class for particle generation.
 * To keep particles modifyable but not requiering to have a large amount of
 * parameters they instead take a ParticleConfig as a single parameter
 */
public class ParticleConfig {

    // ------ Default values ------
    private static final int DEFAULT_POSITION_X = 0;
    private static final int DEFAULT_POSITION_Y = 0;

    private static final double DEFAULT_ANGLE = 0.0;
    private static final double DEFAULT_SPREAD = 0.0;

    // ------ Radius defaults ------
    private static final int DEFAULT_RADIUS_MIN = 1;
    private static final int DEFAULT_RADIUS_MAX = 10;

    // ------ Rectangle dimension defaults ------
    private static final int DEFAULT_RECT_HEIGHT_MIN = 5;
    private static final int DEFAULT_RECT_HEIGHT_MAX = 20;
    private static final int DEFAULT_RECT_WIDTH_MIN = 5;
    private static final int DEFAULT_RECT_WIDTH_MAX = 20;

    // ------ Life span defaults ------
    private static final int DEFAULT_LIFE_MIN = 100;
    private static final int DEFAULT_LIFE_MAX = 200;

    // ------ Alpha transparency defaults ------
    private static final int DEFAULT_ALPHA_MIN = 0;
    private static final int DEFAULT_ALPHA_MAX = 255;

    // ------ Default color ------
    private static final Color DEFAULT_COLOR = Color.WHITE;

    /** Starting position of the particle. */
    public Point position;

    /** Direction angle in radians. */
    public double angle;

    /** Randomness in horizontal movement. */
    public double spread;

    /** Range for circular particle radius (min … max). */
    public ValueRange radiusSize;

    /** Range for rectangular particle height (min … max). */
    public ValueRange rectHeight;

    /** Range for rectangular particle width (min … max). */
    public ValueRange rectWidth;

    /** Range for particle lifespan in frames (min … max). */
    public ValueRange life;

    /** Base color of particles. */
    public Color color;

    /** Range for transparency (alpha) values (min … max). */
    public ValueRange alpha;

    /** Creates a config with reasonable default values. */
    public ParticleConfig() {
        position = new Point(DEFAULT_POSITION_X, DEFAULT_POSITION_Y);
        angle = DEFAULT_ANGLE;
        spread = DEFAULT_SPREAD;
        radiusSize = ValueRange.of(DEFAULT_RADIUS_MIN, DEFAULT_RADIUS_MAX);
        rectHeight = ValueRange.of(DEFAULT_RECT_HEIGHT_MIN, DEFAULT_RECT_HEIGHT_MAX);
        rectWidth = ValueRange.of(DEFAULT_RECT_WIDTH_MIN, DEFAULT_RECT_WIDTH_MAX);
        life = ValueRange.of(DEFAULT_LIFE_MIN, DEFAULT_LIFE_MAX);
        color = DEFAULT_COLOR;
        alpha = ValueRange.of(DEFAULT_ALPHA_MIN, DEFAULT_ALPHA_MAX);
    }

    /** Copy constructor. */
    public ParticleConfig(ParticleConfig other) {
        this.position = new Point(other.position);
        this.angle = other.angle;
        this.spread = other.spread;
        this.radiusSize = other.radiusSize;
        this.rectHeight = other.rectHeight;
        this.rectWidth = other.rectWidth;
        this.life = other.life;
        this.color = other.color;
        this.alpha = other.alpha;
    }
}