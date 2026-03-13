package se.liu.simjolucul.dopeslope.gameobjects;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static se.liu.simjolucul.dopeslope.ConfigLoader.isDebug;

/**
 * an abstract class used to create all obstacles in the game
 * <p>
 * sets upp the basics about shadows, hitboxers and camera shake and
 *  has 2 constructors depending if a BufferedImages is passed as a parameter
 */
public abstract class Obstacle {
    protected Point position;
    protected int width;
    protected int height;
    protected Rectangle hitbox;

    private static final int SHAKE_THRESHOLD = 14;
    /** scales excess speed to shake amount */
    private static final double SHAKE_INTENSITY_FACTOR = 0.1;
    /** minimum pixel shake even at threshold */
    private static final int SHAKE_MIN_AMOUNT = 1;
    /** used to create range [-amount, amount] */
    private static final int SHAKE_RANGE_MULTIPLIER = 2;

    // ----- Hitbox offset constants (relative to obstacle position and size) -----

    private static final int HITBOX_X_OFFSET = -5;
    private static final int HITBOX_Y_OFFSET = -5;

    /** start hitbox at 1/3 of width */
    private static final double HITBOX_X_RATIO = 0.3333333;
    /** hitbox is half the width */
    private static final double HITBOX_WIDTH_RATIO = 0.5;
    /** hitbox height is 1/3 of total height */
    private static final double HITBOX_HEIGHT_RATIO = 0.3333333;

    // ----- Shadow constants -----

    /** shadow height relative to obstacle */
    private static final double SHADOW_HEIGHT_RATIO = 0.3333333;
    /** transparency of shadow */
    private static final int SHADOW_ALPHA = 100;

    private static final Random RND = new Random();

    protected Obstacle(int x, int y, BufferedImage texture) {
        this.position = new Point(x, y);
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        initHitbox();
    }

    protected Obstacle(int x, int y, int width, int height) {
        this.position = new Point(x, y);
        this.width = width;
        this.height = height;
        initHitbox();
    }

    private void initHitbox() {
        int hitboxX = position.x + (int)(width * HITBOX_X_RATIO) + HITBOX_X_OFFSET;
        int hitboxY = position.y + height + HITBOX_Y_OFFSET;
        int hitboxW = (int)(width * HITBOX_WIDTH_RATIO);
        int hitboxH = (int)(height * HITBOX_HEIGHT_RATIO);
        this.hitbox = new Rectangle(hitboxX, hitboxY, hitboxW, hitboxH);
    }

    public Point getPosition() {
        return position;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }


    public int getHeight() { return height; }

    public void update(double playerSpeed) {
        position.y -= (int) playerSpeed;
        hitbox.setLocation(
                position.x + (int)(width * HITBOX_X_RATIO) + HITBOX_X_OFFSET,
                position.y + height + HITBOX_Y_OFFSET
        );
    }

    protected void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    public final void draw(Graphics g, double playerSpeed) {
        Graphics2D g2d = (Graphics2D) g.create();

        if (playerSpeed > SHAKE_THRESHOLD) {
            int shakeAmount = (int)((playerSpeed - SHAKE_THRESHOLD) * SHAKE_INTENSITY_FACTOR) + SHAKE_MIN_AMOUNT;
            int shakeX = RND.nextInt(shakeAmount * SHAKE_RANGE_MULTIPLIER + 1) - shakeAmount;
            int shakeY = RND.nextInt(shakeAmount * SHAKE_RANGE_MULTIPLIER + 1) - shakeAmount;
            g2d.translate(shakeX, shakeY);
        }

        drawObstacle(g2d);
        if (isDebug()) {
            g2d.setColor(new Color(255, 0, 0, 128));
            g2d.fillRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        }

        g2d.dispose();
    }

    protected abstract void drawObstacle(Graphics g);

    public void drawShadow(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        int shadowWidth = width;
        int shadowHeight = (int)(height * SHADOW_HEIGHT_RATIO);
        int shadowX = position.x;
        int shadowY = position.y + height;

        g2d.setColor(new Color(0, 0, 0, SHADOW_ALPHA));
        g2d.fillOval(shadowX, shadowY, shadowWidth, shadowHeight);

        g2d.dispose();
    }
}