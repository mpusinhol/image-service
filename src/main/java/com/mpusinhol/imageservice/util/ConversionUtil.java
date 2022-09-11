package com.mpusinhol.imageservice.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ConversionUtil {

    public static BufferedImage toBufferedImage(byte[] image) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
        return ImageIO.read(inputStream);
    }

    public static byte[] toByteArray(BufferedImage image, String extension) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, extension, outputStream);
        return outputStream.toByteArray();
    }
}
