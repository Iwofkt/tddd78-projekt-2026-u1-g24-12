package se.liu.simjolucul.dopeslope.gameObjects;

import se.liu.simjolucul.dopeslope.game.Direction;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import static se.liu.simjolucul.dopeslope.ConfigLoader.isDebug;

public class Player {

    // --- Constants --- //
    private static final int SIZE = 20;                      // base size for hitbox and drawing
    private static final double PLAYER_ROTATE_SPEED = 0.08;
    private static final double MIN_SPEED = 1.0;
    private static final double MAX_SPEED = 12.0;
    private static final double ACCELERATION = 0.06;
    private static final double SPEED_BOOST = 18.0;          // temporary boost (e.g., after gate)

    // Rotation limits (0.1PI to 0.9PI)
    private static final double MIN_ROTATION = 0.1 * Math.PI;
    private static final double MAX_ROTATION = 0.9 * Math.PI;

    // Shake effect
    private static final double SHAKE_SPEED_THRESHOLD = 10.0;
    private static final double SHAKE_INTENSITY_FACTOR = 0.1;
    private static final double ROTATION_WOBBLE_FACTOR = 0.005;

    // Velocity smoothing factor (0..1)
    private static final double SMOOTHING_FACTOR = 0.1;

    // Ski geometry constants (relative to SIZE)
    private static final double SKI_LENGTH_FACTOR = 3.0;
    private static final double SKI_WIDTH_FACTOR = 1.0 / 6.0;
    private static final double SKI_GAP_EXTRA = 7;
    private static final double SKI_TIP_Y_OFFSET_FACTOR = 1.8;

    // --- INSTANCE FIELDS --- //
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

    // --- PUBLIC METHODS --- //
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

    public void speedBoost() {
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
        double localXL = -skiGap;
        double worldXL = centerX + localXL * Math.cos(angle) - bottomY * Math.sin(angle);
        double worldYL = centerY + localXL * Math.sin(angle) + bottomY * Math.cos(angle);

        // Right ski tip
        double localXR = skiGap - skiWidth;
        double worldXR = centerX + localXR * Math.cos(angle) - bottomY * Math.sin(angle);
        double worldYR = centerY + localXR * Math.sin(angle) + bottomY * Math.cos(angle);

        return new Point[]{
                new Point((int) worldXL, (int) worldYL),
                new Point((int) worldXR-2, (int) worldYR)
        };
    }

    public void update() {
        position.y = spawn.y + (int) (ySpeed * 10);
        shakeTime += 0.2;

        // Accumulate distance traveled (based on vertical speed)
        distanceTraveled += Math.abs(ySpeed);

        hitbox.setLocation(position.x, position.y);

        // min + differance * amount of downwards (if straight down speed is max)
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
            shakeX = Math.sin(shakeTime * 8) * intensity;
            shakeY = Math.cos(shakeTime * 6) * intensity;
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
            drawProceduralSkier(g2d);
        }

        // Restore transform
        g2d.setTransform(oldTransform);

        // Debug: draw hitbox
        if (isDebug()) {
            g2d.setColor(new Color(0, 128, 0, 128));
            g2d.fillRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        }
    }

    private void drawProceduralSkier(Graphics2D g2d) {
        int skiLength = SIZE * 3;
        int skiWidth = SIZE / 6;
        int skiGap = skiWidth + 4;
        int torsoWidth = (int) (SIZE * 1.5);
        int torsoHeight = (int) (SIZE * 1.2);
        int headSize = (int) (SIZE * 0.9);
        int headOffsetY = torsoHeight / 4;
        int goggleWidth = (int) (headSize * 0.6);
        int goggleHeight = headSize / 6;

        // Skis
        g2d.setColor(new Color(25, 25, 25));
        // Left ski
        g2d.fillRoundRect(-skiGap, (int) (-skiLength / 1.8), skiWidth, skiLength, skiWidth, skiWidth);
        // Right ski
        g2d.fillRoundRect(skiGap - skiWidth, (int) (-skiLength / 1.8), skiWidth, skiLength, skiWidth, skiWidth);

        // Torso
        g2d.setColor(new Color(210, 50, 50));
        g2d.fillOval(-torsoWidth / 2, -torsoHeight / 2, torsoWidth, torsoHeight);

        // Head
        g2d.setColor(new Color(40, 40, 40));
        g2d.fillOval(-headSize / 2, -headSize / 2 - headOffsetY, headSize, headSize);

        // Goggles
        g2d.setColor(new Color(5, 88, 165));
        g2d.fillRoundRect(-goggleWidth / 2, -headSize / 2 - headOffsetY,
                goggleWidth, goggleHeight, goggleHeight, goggleHeight);
    }

    // --- GETTERS --- //
    public double getRotation() {
        return rotation;
    }

    public double getYSpeed() {
        return ySpeed;
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

    public int getSize() {
        return SIZE;
    }

    public Point getPosition() {
        return position;
    }

    public double getXSpeed() {
        return xSpeed;
    }
}