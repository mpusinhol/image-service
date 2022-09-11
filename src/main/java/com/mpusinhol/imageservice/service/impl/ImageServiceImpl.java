package com.mpusinhol.imageservice.service.impl;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;
import com.mpusinhol.imageservice.exception.ObjectNotFoundException;
import com.mpusinhol.imageservice.service.BucketService;
import com.mpusinhol.imageservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.mpusinhol.imageservice.util.ConversionUtil.toBufferedImage;
import static com.mpusinhol.imageservice.util.ConversionUtil.toByteArray;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final BucketService bucketService;

    @Override
    public BufferedImage findOptimizedImage(ImagePreDefinedType imagePreDefinedType, String seoName, String filename) throws IOException {
        String key = bucketService.generateKeyPath(imagePreDefinedType, filename);

        try {
            return ImageIO.read(new ByteArrayInputStream(bucketService.getObject(key)));
        } catch (ObjectNotFoundException e) {
            return generateOptimizedImage(imagePreDefinedType, filename);
        }
    }

    private BufferedImage generateOptimizedImage(ImagePreDefinedType imagePreDefinedType, String filename) throws IOException {
        String optimizedKey = bucketService.generateKeyPath(imagePreDefinedType, filename);
        String originalKey = bucketService.generateKeyPath(ImagePreDefinedType.ORIGINAL, filename);

        BufferedImage original;
        try {
            original = ImageIO.read(new ByteArrayInputStream(bucketService.getObject(originalKey)));
        } catch (ObjectNotFoundException e) {
            original = downloadImage(filename);

            if (original == null) {
                throw new ObjectNotFoundException(String.format("Could find requested image %s anywhere.", filename));
            }
        }

        bucketService.putObject(originalKey, toByteArray(original, ImagePreDefinedType.ORIGINAL.getImageType().toString()));
        BufferedImage resizedImage = resizeImage(original, imagePreDefinedType);
        bucketService.putObject(optimizedKey, toByteArray(resizedImage, imagePreDefinedType.getImageType().toString()));

        return resizedImage;
    }

    @Override
    public void deleteImage(ImagePreDefinedType imagePreDefinedType, String filename) {
        if (imagePreDefinedType == ImagePreDefinedType.ORIGINAL) {
            Arrays.stream(ImagePreDefinedType.values())
                    .forEach(preDefinedType -> {
                        String key = bucketService.generateKeyPath(preDefinedType, filename);
                        bucketService.deleteObject(key);
            });
        } else {
            String key = bucketService.generateKeyPath(imagePreDefinedType, filename);
            bucketService.deleteObject(key);
        }
    }

    @Override
    public BufferedImage resizeImage(BufferedImage original, ImagePreDefinedType imagePreDefinedType) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        double quality = imagePreDefinedType.getQuality() / 100;

        Thumbnails.of(original)
                .size(imagePreDefinedType.getWidth(), imagePreDefinedType.getHeight())
                .outputFormat(imagePreDefinedType.getImageType().toString())
                .outputQuality(quality)
                .toOutputStream(outputStream);

        return toBufferedImage(outputStream.toByteArray());
    }

    @Override
    public BufferedImage downloadImage(String filename) throws IOException {
        //Mocking this part due to the short time, otherwise would have used Feign to call the endpoint defined in the properties
        String basePath = "src/main/resources/images/";

        byte[] file;

        if (Files.exists(Paths.get(String.format("%s%s", basePath, filename)))) {
            file = Files.readAllBytes(Paths.get(String.format("%s%s", basePath, filename)));
        } else {
            int random = (int) (Math.random() * (5 - 1) + 1);
            String path = String.format("%sswitzerland%s.jpeg", basePath, random);
            file = Files.readAllBytes(Paths.get(path));
        }

        return toBufferedImage(file);
    }
}
