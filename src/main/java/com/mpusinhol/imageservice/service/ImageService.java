package com.mpusinhol.imageservice.service;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageService {

    BufferedImage findOptimizedImage(ImagePreDefinedType imagePreDefinedType, String seoName, String filename) throws IOException;

    void deleteImage(ImagePreDefinedType imagePreDefinedType, String filename);

    BufferedImage resizeImage(BufferedImage original, ImagePreDefinedType imagePreDefinedType) throws IOException;

    BufferedImage downloadImage(String filename) throws IOException;
}
