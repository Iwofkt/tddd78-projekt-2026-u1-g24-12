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

    // ===== Constants =====

    // ----- Base size -----
    private static final int SIZE = 20;

    // ----- Movement physics -----
    private static final double PLAYER_ROTATE_SPEED = 0.08;
    private static final double MIN_SPEED = 1.0;
    private static final double MAX_SPEED = 12.0;
    private static final double ACCELERATION = 0.06;
    private static final double SPEED_BOOST = 18.0;

    // ----- Rotation limits (0.1PI to 0.9PI) -----
    private static final double MIN_ROTATION = 0.1 * Math.PI;
    private static final double MAX_ROTATION = 0.9 * Math.PI;

    // ----- Shake effect -----
    private static final double SHAKE_SPEED_THRESHOLD = 10.0;
    private static final double SHAKE_INTENSITY_FACTOR = 0.1;
    private static final double ROTATION_WOBBLE_FACTOR = 0.005;
    private static final double SHAKE_TIME_INCREMENT = 0.2;
    private static final double SHAKE_X_FREQUENCY = 8.0;
    private static final double SHAKE_Y_FREQUENCY = 6.0;

    private static final double SMOOTHING_FACTOR = 0.1;

    // ----- Ski geometry constants (relative to SIZE) -----
    private static final double SKI_LENGTH_FACTOR = 3.0;
    private static final double SKI_WIDTH_FACTOR = 1.0 / 6.0;
    /** extra gap between skis */
    private static final double SKI_GAP_EXTRA = 7.0;
    /** used for tip position */
    private static final double SKI_TIP_Y_OFFSET_FACTOR = 1.8;
    /** fine‑tune right ski tip */
    private static final int RIGHT_SKI_TIP_X_OFFSET = -2;

    // ----- Skier body proportions (relative to SIZE) -----
    private static final double TORSO_WIDTH_FACTOR = 1.5;
    private static final double TORSO_HEIGHT_FACTOR = 1.2;
    private static final double HEAD_SIZE_FACTOR = 0.9;
    private static final int HEAD_OFFSET_DIVISOR = 4;
    private static final double GOGGLE_WIDTH_FACTOR = 0.6;
    private static final int GOGGLE_HEIGHT_DIVISOR = 6;

    // ----- Colors -----
    private static final Color SKI_COLOR = new Color(25, 25, 25);
    private static final Color TORSO_COLOR = new Color(210, 50, 50);
    private static final Color HEAD_COLOR = new Color(40, 40, 40);
    private static final Color GOGGLE_COLOR = new Color(5, 88, 165);

    // ----- Vertical position scaling -----
    private static final double VERTICAL_POSITION_SCALE = 10.0;

    // ===== Instance fields =====
    private final Rectangle hitbox;
    private double rotation;
    private double currentSpeed;
    private double xSpeed = 0;
    private double ySpeed = 0;
    private final Point position = new Point(0, 0);
    private final Point spawn = new Point(0, 0);
    private double distanceTraveled = 0;
    private double shakeTime = 0;
    private final BufferedImage texture;

    // ===== Constructor =====
    public Player(Point spawnPosition, double startRotation, BufferedImage texture) {
        this.spawn.setLocation(spawnPosition);
        this.position.setLocation(spawnPosition);
        this.rotation = startRotation;
        this.currentSpeed = MIN_SPEED;
        this.hitbox = new Rectangle(spawnPosition.x, spawnPosition.y, SIZE, SIZE);
        this.texture = texture;
    }

    // ===== Public methods =====

    public void rotate(Direction direction) {
        if (direction == Direction.LEFT && rotation < MAX_ROTATION) {
            rotation += PLAYER_ROTATE_SPEED;
        } else if (direction == Direction.RIGHT && rotation > MIN_ROTATION) {
            rotation -= PLAYER_ROTATE_SPEED;
        }
    }

    public void moveHorizontally(int worldWidth, int margin) {
        int nextX = position.x + (int) xSpeed;
        if (nextX > margin && nextX < worldWidth - margin) {
            position.x = nextX;
        }
    }

    public void boostSpeed() {
        currentSpeed = SPEED_BOOST;
    }

    public void reset(Point spawnPoint, double startRotation) {
        position.setLocation(spawnPoint);
        rotation = startRotation;
        currentSpeed = MIN_SPEED;
        xSpeed = 0;
        ySpeed = 0;
        distanceTraveled = 0;
        shakeTime = 0;
        hitbox.setLocation(spawnPoint.x, spawnPoint.y);
    }

    /**
     * Returns the world coordinates of the tips of both skis.
     * Used for collision detection with gates.
     */
    public Point[] getSkiTipPositions() {
        double skiLength = SIZE * SKI_LENGTH_FACTOR;
        double skiWidth = SIZE * SKI_WIDTH_FACTOR;
        double skiGap = skiWidth + SKI_GAP_EXTRA;
        // tip Y in local coordinates (relative to ski origin)
        double bottomY = -skiLength / SKI_TIP_Y_OFFSET_FACTOR + skiLength;

        double centerX = position.x + SIZE / 2.0;
        double centerY = position.y + SIZE / 2.0;
        double angle = rotation + Math.PI / 2; // because the skier image is rotated

        // Left ski tip
        double localXLeft = -skiGap;
        double worldXLeft = centerX + localXLeft * Math.cos(angle) - bottomY * Math.sin(angle);
        double worldYLeft = centerY + localXLeft * Math.sin(angle) + bottomY * Math.cos(angle);

        // Right ski tip
        double localXRight = skiGap - skiWidth;
        double worldXRight = centerX + localXRight * Math.cos(angle) - bottomY * Math.sin(angle);
        double worldYRight = centerY + localXRight * Math.sin(angle) + bottomY * Math.cos(angle);

        return new Point[]{
                new Point((int) worldXLeft, (int) worldYLeft),
                new Point((int) worldXRight + RIGHT_SKI_TIP_X_OFFSET, (int) worldYRight)
        };
    }

    public void update() {
        position.y = spawn.y + (int) (ySpeed * VERTICAL_POSITION_SCALE);
        shakeTime += SHAKE_TIME_INCREMENT;

        // Accumulate distance traveled (based on vertical speed)
        distanceTraveled += Math.abs(ySpeed);

        hitbox.setLocation(position.x, position.y);

        // min + difference * amount of downwards (if straight down speed is max)
        double targetSpeed = MIN_SPEED + (MAX_SPEED - MIN_SPEED) * Math.sin(rotation);

        // Accelerate or decelerate towards target speed
        if (currentSpeed < targetSpeed) {
            currentSpeed += ACCELERATION;
        } else if (currentSpeed > targetSpeed) {
            currentSpeed -= ACCELERATION;
        }

        // Desired velocity components based on current speed and rotation
        double targetX = currentSpeed * Math.cos(rotation);
        double targetY = currentSpeed * Math.sin(rotation);

        // Smoothly adjust actual velocities
        xSpeed += (targetX - xSpeed) * SMOOTHING_FACTOR;
        ySpeed += (targetY - ySpeed) * SMOOTHING_FACTOR;
    }

    public void draw(Graphics2D g2d) {
        int centerX = position.x + SIZE / 2;
        int centerY = position.y + SIZE / 2;

        // Shake effect at high speed
        double shakeX = 0, shakeY = 0;
        if (currentSpeed > SHAKE_SPEED_THRESHOLD) {
            double intensity = (currentSpeed - SHAKE_SPEED_THRESHOLD) * SHAKE_INTENSITY_FACTOR;
            shakeX = Math.sin(shakeTime * SHAKE_X_FREQUENCY) * intensity;
            shakeY = Math.cos(shakeTime * SHAKE_Y_FREQUENCY) * intensity;
        }

        // Slight rotation wobble at high speed
        double drawRotation = rotation;
        drawRotation += Math.sin(shakeTime) * ROTATION_WOBBLE_FACTOR * currentSpeed;

        // Save original transform
        AffineTransform oldTransform = g2d.getTransform();

        // Move to player center, apply shake, then rotate
        g2d.translate(centerX + shakeX, centerY + shakeY);
        g2d.rotate(drawRotation + Math.PI / 2);

        if (texture != null) {
            // Draw texture centered on (0,0)
            g2d.drawImage(texture, -texture.getWidth() / 2, -texture.getHeight() / 2, null);
        } else {
            drawFallbackSkier(g2d);
        }

        // Restore original transform (safe because we saved it)
        g2d.setTransform(oldTransform);

        // Debug: draw hitbox
        if (isDebug()) {
            g2d.setColor(new Color(0, 128, 0, 128));
            g2d.fillRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        }
    }

    private void drawFallbackSkier(Graphics2D g2d) {
        int skiLength = (int) (SIZE * SKI_LENGTH_FACTOR);
        int skiWidth = (int) (SIZE * SKI_WIDTH_FACTOR);
        int skiGap = (int) (skiWidth + SKI_GAP_EXTRA);

        int torsoWidth = (int) (SIZE * TORSO_WIDTH_FACTOR);
        int torsoHeight = (int) (SIZE * TORSO_HEIGHT_FACTOR);
        int headSize = (int) (SIZE * HEAD_SIZE_FACTOR);
        int headOffsetY = torsoHeight / HEAD_OFFSET_DIVISOR;
        int goggleWidth = (int) (headSize * GOGGLE_WIDTH_FACTOR);
        int goggleHeight = headSize / GOGGLE_HEIGHT_DIVISOR;

        // Skis
        g2d.setColor(SKI_COLOR);
        // Left ski
        g2d.fillRoundRect(-skiGap,
                          (int) (-skiLength / SKI_TIP_Y_OFFSET_FACTOR),
                          skiWidth, skiLength, skiWidth, skiWidth);
        // Right ski
        g2d.fillRoundRect(skiGap - skiWidth,
                          (int) (-skiLength / SKI_TIP_Y_OFFSET_FACTOR),
                          skiWidth, skiLength, skiWidth, skiWidth);

        // Torso
        g2d.setColor(TORSO_COLOR);
        g2d.fillOval(-torsoWidth / 2, -torsoHeight / 2, torsoWidth, torsoHeight);

        // Head
        g2d.setColor(HEAD_COLOR);
        g2d.fillOval(-headSize / 2, -headSize / 2 - headOffsetY, headSize, headSize);

        // Goggles
        g2d.setColor(GOGGLE_COLOR);
        g2d.fillRoundRect(-goggleWidth / 2,
                          -headSize / 2 - headOffsetY,
                          goggleWidth, goggleHeight,
                          goggleHeight, goggleHeight);
    }

    // ===== Getters =====

    @SuppressWarnings("unused")
    public double getRotation() {
        return rotation;
    }

    /**
     * Returns the current velocity as a Point2D.Double (x = horizontal, y = vertical).
     */
    public Point2D.Double getSpeed() {
        return new Point2D.Double(xSpeed, ySpeed);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public double getMaxSpeed() {
        return MAX_SPEED;
    }

    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }
}