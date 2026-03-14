package se.liu.simjolucul.dopeslope.handlers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for loading and scaling texture images from the resource folder.
 * <p>
 * Textures are stored in {@code /textures/<texturePack>/<type>.png} and are loaded
 * as {@link BufferedImage} objects. The class provides methods to load an image
 * as-is or to scale it by integer factors.
 * </p>
 */
public class ImageLoader {

    public static BufferedImage loadTexture(String texturePack, String type) {

        String path = File.separator + "textures" + File.separator + texturePack + File.separator + type + ".png";

        try (InputStream is = ImageLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                System.out.println("Could not find: " + path);
                return null;
            }
            BufferedImage image = ImageIO.read(is);
            if (image == null) {
                System.out.println("Failed to read image (unsupported format or corrupt file): " + path);
            }
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage loadTextureSize(String texturePack, String type, int widthFactor, int heightFactor) {
        BufferedImage img = loadTexture(texturePack, type);
        if (img == null) {
            return null;
        }

        int newWidth = img.getWidth() * widthFactor;
        int newHeight = img.getHeight() * heightFactor;
        BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = scaled.createGraphics();
        g2d.drawImage(img, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return scaled;
    }
}