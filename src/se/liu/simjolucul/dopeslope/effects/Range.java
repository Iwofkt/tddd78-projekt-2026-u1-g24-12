package se.liu.simjolucul.dopeslope.effects;

public class Range {
    public int min;
    public int max;

    public IntRange(int min, int max) {
	this.min = min;
	this.max = max;
    }

    public IntRange(IntRange other) {
	this(other.min, other.max);
    }

    // optional: add validation (e.g., min <= max), getters, etc.
}
