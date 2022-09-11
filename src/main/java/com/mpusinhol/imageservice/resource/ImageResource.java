package com.mpusinhol.imageservice.resource;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;
import com.mpusinhol.imageservice.enumeration.ImageType;
import com.mpusinhol.imageservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.mpusinhol.imageservice.util.ConversionUtil.toByteArray;

@RestController
@RequestMapping(value = "/image")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ImageResource {

    private final ImageService imageService;

    @GetMapping(value = {"/show/{pre-defined-type}", "/show/{pre-defined-type}/{seo-name}"})
    public ResponseEntity<byte[]> findImage(
            @PathVariable(name = "pre-defined-type") String preDefinedType,
            @PathVariable(name = "seo-name", required = false) String seoName,
            @RequestParam(name = "reference") String filename) throws IOException {

        ImagePreDefinedType imagePreDefinedType = ImagePreDefinedType.valueOf(preDefinedType.toUpperCase());
        BufferedImage image = imageService.findOptimizedImage(imagePreDefinedType, seoName, filename);

        return ResponseEntity.ok()
                .contentType(imagePreDefinedType.getImageType() == ImageType.JPG ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG)
                .body(toByteArray(image, imagePreDefinedType.getImageType().toString()));
    }

    @DeleteMapping("/flush/{pre-defined-type}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable(name = "pre-defined-type") String preDefinedType,
            @RequestParam(name = "reference") String filename) {

        ImagePreDefinedType imagePreDefinedType = ImagePreDefinedType.valueOf(preDefinedType.toUpperCase());
        imageService.deleteImage(imagePreDefinedType, filename);

        return ResponseEntity.noContent().build();
    }
}
