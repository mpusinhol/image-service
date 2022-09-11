package com.mpusinhol.imageservice.resource;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;
import com.mpusinhol.imageservice.exception.InternalServerErrorException;
import com.mpusinhol.imageservice.exception.ObjectNotFoundException;
import com.mpusinhol.imageservice.service.ImageService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.mpusinhol.imageservice.util.ConversionUtil.toBufferedImage;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@WebMvcTest(ImageResource.class)
@AutoConfigureMockMvc
public class ImageResourceTest {

    @MockBean
    private ImageService imageService;

    @Autowired
    private MockMvc mockMvc;

    private static final String IMAGE_PATH = "src/test/resources/com/mpusinhol/imageservice/resource/switzerland1.jpeg";

    @Nested
    @DisplayName("Find optimized image")
    class FindOptimizedImage {
        @Test
        @SneakyThrows
        @DisplayName("Happy flow")
        void findOptimizedImageHappyFlow() {
            String filename = "switzerland1.jpg";
            ImagePreDefinedType imagePreDefinedType = ImagePreDefinedType.THUMBNAIL;

            assertDoesNotThrow(() -> {
                byte[] file = Files.readAllBytes(Paths.get(new File(IMAGE_PATH).getAbsoluteFile().toURI()));
                BufferedImage image = toBufferedImage(file);
                when(imageService.findOptimizedImage(imagePreDefinedType, "seoName", filename)).thenReturn(image);
            });

            String endpoint = String.format("/image/show/%s/seoName?reference=%s", imagePreDefinedType, filename);

            mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        @SneakyThrows
        @DisplayName("Original not found")
        void findOptimizedImageNotFound() {
            String filename = "switzerland1.jpg";
            ImagePreDefinedType imagePreDefinedType = ImagePreDefinedType.THUMBNAIL;

            when(imageService.findOptimizedImage(imagePreDefinedType, "seoName", filename))
                    .thenThrow(ObjectNotFoundException.class);

            String endpoint = String.format("/image/show/%s/seoName?reference=%s", imagePreDefinedType, filename);

            mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Internal error")
        void findOptimizedImageInternalError() {
            String filename = "switzerland1.jpg";
            ImagePreDefinedType imagePreDefinedType = ImagePreDefinedType.THUMBNAIL;

            when(imageService.findOptimizedImage(imagePreDefinedType, "seoName", filename))
                    .thenThrow(InternalServerErrorException.class);

            String endpoint = String.format("/image/show/%s/seoName?reference=%s", imagePreDefinedType, filename);

            mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                    .andExpect(MockMvcResultMatchers.status().isInternalServerError());
        }

        @Test
        @SneakyThrows
        @DisplayName("Non-existing pre defined type")
        void findOptimizedImageNonExistingPreDefinedType() {
            String filename = "switzerland1.jpg";

            String endpoint = String.format("/image/show/invalidType/seoName?reference=%s", filename);

            mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Flush image")
    class FlushImage {
        @Test
        @SneakyThrows
        @DisplayName("Happy flow")
        void happyFlow() {
            String filename = "switzerland1.jpg";
            ImagePreDefinedType imagePreDefinedType = ImagePreDefinedType.THUMBNAIL;

            doNothing().when(imageService).deleteImage(any(), any());

            String endpoint = String.format("/image/flush/%s?reference=%s", imagePreDefinedType, filename);

            mockMvc.perform(MockMvcRequestBuilders.delete(endpoint))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());
        }

        @Test
        @SneakyThrows
        @DisplayName("Non-existing pre defined type")
        void nonExistingPreDefinedType() {
            String filename = "switzerland1.jpg";

            String endpoint = String.format("/image/flush/INVALIDTYPE?reference=%s", filename);

            mockMvc.perform(MockMvcRequestBuilders.delete(endpoint))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }
}
