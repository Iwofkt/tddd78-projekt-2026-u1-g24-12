package se.liu.simjolucul.dopeslope.gameobjects;

import se.liu.simjolucul.dopeslope.game.Direction;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import static se.liu.simjolucul.dopeslope.ConfigLoader.isDebug;

/**
 * Represents the player (skier) in the game.
 * Handles movement, rotation, speed, and drawing of the skier.
 * The player can be drawn either with a provided texture or with a
 * simple procedural fallback shape.
 */
public class Player {

    // ====== Constants ======

    /** Base size of the player hitbox and for scaling proportions */
    public static class Size {
        public static final int BASE = 20;
    }

    /** Movement physics constants */
    public static class Movement {
        public static final double ROTATE_SPEED = 0.08;
        public static final double MIN_SPEED = 1.0;
        public static final double MAX_SPEED = 12.0;
        public static final double ACCELERATION = 0.06;
        public static final double BOOST_SPEED = 18.0;
        public static final double SMOOTHING_FACTOR = 0.1;
    }

    /** Rotation limits (0.1PI to 0.9PI) */
    public static class RotationLimits {
        public static final double MIN = 0.1 * Math.PI;
        public static final double MAX = 0.9 * Math.PI;
    }

    /** Shake effect constants for high-speed visuals */
    public static class Shake {
        public static final double SPEED_THRESHOLD = 10.0;
        public static final double INTENSITY_FACTOR = 0.1;
        public static final double WOBBLE_FACTOR = 0.005;
        public static final double TIME_INCREMENT = 0.2;
        public static final double X_FREQUENCY = 8.0;
        public static final double Y_FREQUENCY = 6.0;
    }

    /** Ski geometry constants (relative to Size.BASE) */
    public static class SkiGeometry {
        public static final double LENGTH_FACTOR = 3.0;
        public static final double WIDTH_FACTOR = 0.166666666;
        /** extra gap between skis */
        public static final double GAP_EXTRA = 7.0;
        /** used for tip position */
        public static final double TIP_Y_OFFSET_FACTOR = 1.8;
        /** fine‑tune right ski tip */
        public static final int RIGHT_TIP_X_OFFSET = -2;
    }

    /** Skier body proportions (relative to Size.BASE) */
    public static class BodyProportions {
        public static final double TORSO_WIDTH_FACTOR = 1.5;
        public static final double TORSO_HEIGHT_FACTOR = 1.2;
        public static final double HEAD_SIZE_FACTOR = 0.9;
        public static final int HEAD_OFFSET_DIVISOR = 4;
        public static final double GOGGLE_WIDTH_FACTOR = 0.6;
        public static final int GOGGLE_HEIGHT_DIVISOR = 6;
    }

    /** Color constants for fallback drawing */
    public static class Colors {
        public static final Color SKI = new Color(25, 25, 25);
        public static final Color TORSO = new Color(210, 50, 50);
        public static final Color HEAD = new Color(40, 40, 40);
        public static final Color GOGGLE = new Color(5, 88, 165);
    }

    /** Vertical position scaling factor */
    public static final double VERTICAL_POSITION_SCALE = 10.0;

    // ====== Instance fields ======
    private final Rectangle hitbox;
    private double rotation;
    private double currentSpeed;
    /** Combined velocity vector (x = horizontal, y = vertical) */
    private final Point2D.Double velocity = new Point2D.Double(0, 0);
    private final Point position = new Point(0, 0);
    private final Point spawn = new Point(0, 0);
    private double distanceTraveled = 0;
    private double shakeTime = 0;
    private final BufferedImage texture;

    // ====== Constructor ======
    public Player(Point spawnPosition, double startRotation, BufferedImage texture) {
        this.spawn.setLocation(spawnPosition);
        this.position.setLocation(spawnPosition);
        this.rotation = startRotation;
        this.currentSpeed = Movement.MIN_SPEED;
        this.hitbox = new Rectangle(spawnPosition.x, spawnPosition.y, Size.BASE, Size.BASE);
        this.texture = texture;
    }

    // ====== Public methods ======

    public void rotate(Direction direction) {
        if (direction == Direction.LEFT && rotation < RotationLimits.MAX) {
            rotation += Movement.ROTATE_SPEED;
        } else if (direction == Direction.RIGHT && rotation > RotationLimits.MIN) {
            rotation -= Movement.ROTATE_SPEED;
        }
    }

    public void moveHorizontally(int worldWidth, int margin) {
        int nextX = position.x + (int) velocity.x;
        if (nextX > margin && nextX < worldWidth - margin) {
            position.x = nextX;
        }
    }

    public void boostSpeed() {
        currentSpeed = Movement.BOOST_SPEED;
    }

    public void reset(Point spawnPoint, double startRotation) {
        position.setLocation(spawnPoint);
        rotation = startRotation;
        currentSpeed = Movement.MIN_SPEED;
        velocity.setLocation(0, 0);
        distanceTraveled = 0;
        shakeTime = 0;
        hitbox.setLocation(spawnPoint.x, spawnPoint.y);
    }

    /**
     * Returns the world coordinates of the tips of both skis.
     * Used for collision detection with gates.
     */
    public Point[] getSkiTipPositions() {
        double skiLength = Size.BASE * SkiGeometry.LENGTH_FACTOR;
        double skiWidth = Size.BASE * SkiGeometry.WIDTH_FACTOR;
        double skiGap = skiWidth + SkiGeometry.GAP_EXTRA;
        // tip Y in local coordinates (relative to ski origin)
        double bottomY = -skiLength / SkiGeometry.TIP_Y_OFFSET_FACTOR + skiLength;

        double centerX = position.x + Size.BASE / 2.0;
        double centerY = position.y + Size.BASE / 2.0;
        double angle = rotation + Math.PI / 2; // because the skier image is rotated

        double[] localX = {-skiGap, skiGap - skiWidth};
        double[] worldX = new double[2];
        double[] worldY = new double[2];

        for (int i = 0; i < 2; i++) {
            worldX[i] = centerX + localX[i] * Math.cos(angle) - bottomY * Math.sin(angle);
            worldY[i] = centerY + localX[i] * Math.sin(angle) + bottomY * Math.cos(angle);
        }

        // Apply right ski tip offset
        return new Point[]{
                new Point((int) worldX[0], (int) worldY[0]),
                new Point((int) worldX[1] + SkiGeometry.RIGHT_TIP_X_OFFSET, (int) worldY[1])
        };
    }

    public void update() {
        position.y = spawn.y + (int) (velocity.y * VERTICAL_POSITION_SCALE);
        shakeTime += Shake.TIME_INCREMENT;

        // Accumulate distance traveled (based on vertical speed)
        distanceTraveled += Math.abs(velocity.y);

        hitbox.setLocation(position.x, position.y);

        // Calculate target speed based on rotation (more downward = faster)
        double targetSpeed = Movement.MIN_SPEED +
                             (Movement.MAX_SPEED - Movement.MIN_SPEED) * Math.sin(rotation);

        // Accelerate or decelerate towards target speed
        if (currentSpeed < targetSpeed) {
            currentSpeed += Movement.ACCELERATION;
        } else if (currentSpeed > targetSpeed) {
            currentSpeed -= Movement.ACCELERATION;
        }

        // Desired velocity components based on current speed and rotation
        double targetX = currentSpeed * Math.cos(rotation);
        double targetY = currentSpeed * Math.sin(rotation);

        // Smoothly adjust actual velocities
        velocity.x += (targetX - velocity.x) * Movement.SMOOTHING_FACTOR;
        velocity.y += (targetY - velocity.y) * Movement.SMOOTHING_FACTOR;
    }

    public void draw(Graphics2D g2d) {
        int centerX = position.x + Size.BASE / 2;
        int centerY = position.y + Size.BASE / 2;

        // Shake effect at high speed
        Point2D.Double shake = new Point2D.Double(0, 0);
        if (currentSpeed > Shake.SPEED_THRESHOLD) {
            double intensity = (currentSpeed - Shake.SPEED_THRESHOLD) * Shake.INTENSITY_FACTOR;
            shake.x = Math.sin(shakeTime * Shake.X_FREQUENCY) * intensity;
            shake.y = Math.cos(shakeTime * Shake.Y_FREQUENCY) * intensity;
        }

        // Slight rotation wobble at high speed
        double drawRotation = rotation;
        drawRotation += Math.sin(shakeTime) * Shake.WOBBLE_FACTOR * currentSpeed;

        // Save original transform
        AffineTransform old = g2d.getTransform();

        // Move to player center, apply shake, then rotate
        g2d.translate(centerX + shake.x, centerY + shake.y);
        g2d.rotate(drawRotation + Math.PI / 2);

        if (texture != null) {
            // Draw texture centered on (0,0)
            g2d.drawImage(texture, -texture.getWidth() / 2, -texture.getHeight() / 2, null);
        } else {
            drawFallbackSkier(g2d);
        }

        // Restore original transform
        g2d.setTransform(old);

        // Debug: draw hitbox
        if (isDebug()) {
            g2d.setColor(new Color(0, 128, 0, 128));
            g2d.fillRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        }
    }

    private void drawFallbackSkier(Graphics2D g2d) {
        int skiLength = (int) (Size.BASE * SkiGeometry.LENGTH_FACTOR);
        int skiWidth = (int) (Size.BASE * SkiGeometry.WIDTH_FACTOR);
        int skiGap = (int) (skiWidth + SkiGeometry.GAP_EXTRA);

        int torsoWidth = (int) (Size.BASE * BodyProportions.TORSO_WIDTH_FACTOR);
        int torsoHeight = (int) (Size.BASE * BodyProportions.TORSO_HEIGHT_FACTOR);
        int headSize = (int) (Size.BASE * BodyProportions.HEAD_SIZE_FACTOR);
        int headOffsetY = torsoHeight / BodyProportions.HEAD_OFFSET_DIVISOR;
        int goggleWidth = (int) (headSize * BodyProportions.GOGGLE_WIDTH_FACTOR);
        int goggleHeight = headSize / BodyProportions.GOGGLE_HEIGHT_DIVISOR;

        // Skis
        g2d.setColor(Colors.SKI);

        // Combine ski positions into an array
        int[] skiXPositions = {-skiGap, skiGap - skiWidth};
        int skiYPosition = (int) (-skiLength / SkiGeometry.TIP_Y_OFFSET_FACTOR);

        for (int skiX : skiXPositions) {
            g2d.fillRoundRect(skiX, skiYPosition, skiWidth, skiLength, skiWidth, skiWidth);
        }

        // Torso
        g2d.setColor(Colors.TORSO);
        g2d.fillOval(-torsoWidth / 2, -torsoHeight / 2, torsoWidth, torsoHeight);

        // Head
        g2d.setColor(Colors.HEAD);
        g2d.fillOval(-headSize / 2, -headSize / 2 - headOffsetY, headSize, headSize);

        // Goggles
        g2d.setColor(Colors.GOGGLE);
        g2d.fillRoundRect(-goggleWidth / 2,
                          -headSize / 2 - headOffsetY,
                          goggleWidth, goggleHeight,
                          goggleHeight, goggleHeight);
    }

    // ===== Getters =====

    /**
     * Returns the current velocity as a Point2D.Double (x = horizontal, y = vertical).
     */
    public Point2D.Double getSpeed() {
        return new Point2D.Double(velocity.x, velocity.y);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public double getMaxSpeed() {
        return Movement.MAX_SPEED;
    }

    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }
}