package se.liu.simjolucul.dopeslope.effects;
import java.awt.Color;
import java.awt.Point;
import java.time.temporal.ValueRange;

/**
 * Configuration for particle generation.
 * Uses JDK classes to group related fields and reduce clutter.
 */
public class ParticleConfig {

    /** Starting position of the particle. */
    public Point position;

    /** Direction angle in radians. */
    public double angle;

    /** Randomness in horizontal movement. */
    public double spread;

    /** Range for circular particle radius (min … max). */
    public ValueRange radiusSize;         // replaces radiusSizeMin/Max

    /** Range for rectangular particle height (min … max). */
    public ValueRange rectHeight;         // replaces recHeightMin/Max

    /** Range for rectangular particle width (min … max). */
    public ValueRange rectWidth;          // replaces recWidthMin/Max

    /** Range for particle lifespan in frames (min … max). */
    public ValueRange life;                // replaces lifeMin/Max

    /** Base color of particles. */
    public Color color;

    /** Range for transparency (alpha) values (min … max). */
    public ValueRange alpha;               // replaces alphaMin/Max

    /** Creates a config with reasonable default values. */
    public ParticleConfig() {
        position = new Point(0, 0);
        angle = 0.0;
        spread = 0.0;
        radiusSize = ValueRange.of(1, 10);        // default radius 1–10
        rectHeight = ValueRange.of(5, 20);        // default height 5–20
        rectWidth = ValueRange.of(5, 20);         // default width 5–20
        life = ValueRange.of(100, 200);           // default life 100–200
        color = Color.WHITE;
        alpha = ValueRange.of(0, 255);            // default alpha 0–255
    }

    /** Copy constructor. */
    public ParticleConfig(ParticleConfig other) {
        // Point is mutable; create a new instance to preserve encapsulation
        this.position = new Point(other.position);
        this.angle = other.angle;
        this.spread = other.spread;
        // ValueRange is immutable and thread‑safe, so we can share references
        this.radiusSize = other.radiusSize;
        this.rectHeight = other.rectHeight;
        this.rectWidth = other.rectWidth;
        this.life = other.life;
        this.color = other.color;       // Color is immutable
        this.alpha = other.alpha;
    }
}