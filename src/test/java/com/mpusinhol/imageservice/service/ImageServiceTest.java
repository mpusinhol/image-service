package com.mpusinhol.imageservice.service;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;
import com.mpusinhol.imageservice.exception.ObjectNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.mpusinhol.imageservice.util.ConversionUtil.toByteArray;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @MockBean
    private BucketService bucketService;

    @Nested
    @DisplayName("Find optimized image test")
    class FindOptimizedImageTests {
        @Test
        @DisplayName("Image already in the bucket")
        void imageAlreadyInTheBucket() {
            assertDoesNotThrow(() -> {
                String filename = "filename.jpeg";
                String key = "/thumbnail/file/filename.jpeg";

                BufferedImage original = imageService.downloadImage("switzerland1.jpeg");
                assertNotNull(original);
                byte[] byteArray = toByteArray(original, ImagePreDefinedType.ORIGINAL.getImageType().toString());

                when(bucketService.generateKeyPath(ImagePreDefinedType.THUMBNAIL, filename)).thenReturn(key);
                when(bucketService.getObject(key)).thenReturn(byteArray);

                assertDoesNotThrow(() -> {
                    BufferedImage image = imageService.findOptimizedImage(ImagePreDefinedType.THUMBNAIL,"", filename);

                    assertNotNull(image);
                    assertEquals(original.getWidth(), image.getWidth());
                    assertEquals(original.getHeight(), image.getHeight());
                    assertEquals(original.getType(), image.getType());
                });
            });
        }

        @Test
        @SneakyThrows
        @DisplayName("Optimized image not in the bucket - original from bucket")
        void optimizedNotInBucketOriginalFromBucket() {
            String filename = "filename.jpeg";
            String key = "/thumbnail/file/filename.jpeg";
            String originalKey = "/original/file/filename.jpeg";
            String etag = "etag";

            BufferedImage original = imageService.downloadImage("switzerland1.jpeg");
            assertNotNull(original);
            byte[] byteArray = toByteArray(original, ImagePreDefinedType.ORIGINAL.getImageType().toString());

            when(bucketService.generateKeyPath(ImagePreDefinedType.THUMBNAIL, filename)).thenReturn(key);
            when(bucketService.generateKeyPath(ImagePreDefinedType.ORIGINAL, filename)).thenReturn(originalKey);
            when(bucketService.getObject(key)).thenThrow(ObjectNotFoundException.class);
            when(bucketService.getObject(originalKey)).thenReturn(byteArray);
            when(bucketService.putObject(originalKey, byteArray)).thenReturn(etag);
            when(bucketService.putObject(eq(key), any())).thenReturn(etag);

            BufferedImage image = imageService.findOptimizedImage(ImagePreDefinedType.THUMBNAIL,"", filename);

            assertNotNull(image);
            assertNotEquals(original.getWidth(), image.getWidth());
            assertNotEquals(original.getHeight(), image.getHeight());
            assertEquals(original.getType(), image.getType());
        }

        @Test
        @SneakyThrows
        @DisplayName("Optimized image not in the bucket - original from download")
        void optimizedNotInBucketOriginalFromDownload() {
            String filename = "switzerland1.jpeg";
            String key = "/thumbnail/swit/zerl/switzerland1.jpeg";
            String originalKey = "/original/swit/zerl/switzerland1.jpeg";
            String etag = "etag";

            BufferedImage original = imageService.downloadImage(filename);
            assertNotNull(original);
            byte[] byteArray = toByteArray(original, ImagePreDefinedType.ORIGINAL.getImageType().toString());

            when(bucketService.generateKeyPath(ImagePreDefinedType.THUMBNAIL, filename)).thenReturn(key);
            when(bucketService.generateKeyPath(ImagePreDefinedType.ORIGINAL, filename)).thenReturn(originalKey);
            when(bucketService.getObject(key)).thenThrow(ObjectNotFoundException.class);
            when(bucketService.getObject(originalKey)).thenThrow(ObjectNotFoundException.class);
            when(bucketService.putObject(originalKey, byteArray)).thenReturn(etag);
            when(bucketService.putObject(eq(key), any())).thenReturn(etag);

            BufferedImage image = imageService.findOptimizedImage(ImagePreDefinedType.THUMBNAIL,"", filename);

            assertNotNull(image);
            assertNotEquals(original.getWidth(), image.getWidth());
            assertNotEquals(original.getHeight(), image.getHeight());
            assertEquals(original.getType(), image.getType());
        }
    }

    @Nested
    @DisplayName("Delete image test")
    class DeleteImageTests {
        @Test
        @DisplayName("Not original type")
        void notOriginalType() {
            String filename = "filename.jpeg";
            String key = "/thumbnail/file/filename.jpeg";

            when(bucketService.generateKeyPath(ImagePreDefinedType.THUMBNAIL, filename)).thenReturn(key);
            doNothing().when(bucketService).deleteObject(key);

            imageService.deleteImage(ImagePreDefinedType.THUMBNAIL, filename);

            verify(bucketService, times(1)).deleteObject(key);
        }

        @Test
        @DisplayName("Original type")
        void originalType() {
            String filename = "filename.jpeg";

            Arrays.stream(ImagePreDefinedType.values())
                    .forEach(imagePreDefinedType -> {
                        String key = String.format("/%s/file/filename.jpeg", imagePreDefinedType.toString().toLowerCase());

                        when(bucketService.generateKeyPath(imagePreDefinedType, filename)).thenReturn(key);
                        doNothing().when(bucketService).deleteObject(key);
            });

            imageService.deleteImage(ImagePreDefinedType.ORIGINAL, filename);

            verify(bucketService, times(ImagePreDefinedType.values().length)).deleteObject(any(String.class));
        }
    }

    @Nested
    @DisplayName("Resize image test")
    class ResizeImageTests {
        @Test
        @DisplayName("All pre defined types")
        void happyFlow() {
            assertDoesNotThrow(() -> {
                BufferedImage image = imageService.downloadImage("switzerland1.jpeg");
                assertNotNull(image);

                Arrays.stream(ImagePreDefinedType.values()).forEach(imagePreDefinedType ->
                        assertDoesNotThrow(() -> {
                            BufferedImage resizedImage = imageService.resizeImage(image, ImagePreDefinedType.THUMBNAIL);

                            assertNotNull(resizedImage);
                            assertEquals(image.getType(), resizedImage.getType());
                            assertNotEquals(image.getWidth(), resizedImage.getWidth());
                            assertNotEquals(image.getHeight(), resizedImage.getHeight());
                        }));
            });
        }
    }

    @Nested
    @DisplayName("Download image test")
    class DownloadImageTests {
        @Test
        @DisplayName("Filename in the list")
        void filenameInList() {
            String basePath = "src/main/resources/images/";
            String filename = "switzerland1.jpeg";

            assertTrue(Files.exists(Paths.get(String.format("%s%s", basePath, filename))));
            assertDoesNotThrow(() -> {
                BufferedImage bufferedImage = imageService.downloadImage(filename);
                assertNotNull(bufferedImage);
            });
        }

        @Test
        @DisplayName("Filename not in the list")
        void filenameNotInList() {
            String basePath = "src/main/resources/images/";
            String filename = "dummy.jpeg";

            assertFalse(Files.exists(Paths.get(String.format("%s%s", basePath, filename))));
            assertDoesNotThrow(() -> {
                BufferedImage bufferedImage = imageService.downloadImage(filename);
                assertNotNull(bufferedImage);
            });
        }

        @Test
        @DisplayName("Filename null")
        void filenameNull() {
            assertDoesNotThrow(() -> {
                BufferedImage bufferedImage = imageService.downloadImage(null);
                assertNotNull(bufferedImage);
            });
        }
    }

}
