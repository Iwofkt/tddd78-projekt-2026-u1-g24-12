package se.liu.simjolucul.dopeslope.handlers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {

    public static BufferedImage loadTexture(String texturePack, String type) {
        String path = "/textures/" + texturePack + "/" + type + ".png";

        try (InputStream is = ImageLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                System.out.println("Could not find: " + path);
                return null;
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage loadTextureSize(String texturePack, String type,int width,int height) {
        BufferedImage img = loadTexture(texturePack, type);
        assert img != null;
        BufferedImage scaled = new BufferedImage(
                img.getWidth() * width,
                img.getHeight() * height,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = scaled.createGraphics();
        g2d.drawImage(
                img,
                0,
                0,
                img.getWidth() * width,
                img.getHeight() * height,
                null);
        g2d.dispose();

        return scaled;
    }
}