package se.liu.simjolucul.dopeslope.gameObjects;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static se.liu.simjolucul.dopeslope.ConfigLoader.isDebug;

public abstract class Obstacle {
    protected Point position;
    protected int width;
    protected int height;
    protected Rectangle hitbox;
    protected BufferedImage texture;

    private static final int SHAKE_THRESHOLD = 14;
    private static final Random random = new Random();

    public Obstacle(int x, int y, BufferedImage texture) {
        this.position = new Point(x, y);
        this.texture = texture;
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        this.hitbox = new Rectangle(x+width/3-5, y+height-5, width/2, height/3);
    }
    public Obstacle(int x, int y, int width, int height) {
        this.position = new Point(x, y);
        this.width = width;
        this.height = height;
        this.hitbox = new Rectangle(x+width/3-5, y+height-5, width/2, height/3);
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
        hitbox.setLocation(position.x+width/3-5, position.y+height-5);
    }

    public final void draw(Graphics g, double playerSpeed) {

        Graphics2D g2d = (Graphics2D) g.create();

        if (playerSpeed > SHAKE_THRESHOLD) {

            int shakeAmount = (int)((playerSpeed - SHAKE_THRESHOLD) * 0.1) + 1;

            int shakeX = random.nextInt(shakeAmount * 2 + 1) - shakeAmount;
            int shakeY = random.nextInt(shakeAmount * 2 + 1) - shakeAmount;

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
        int shadowHeight = height / 3;

        int shadowX = position.x;
        int shadowY = position.y + height;

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillOval(shadowX, shadowY, shadowWidth, shadowHeight);

        g2d.dispose();
    }
}