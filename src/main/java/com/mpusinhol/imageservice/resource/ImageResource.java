package com.mpusinhol.imageservice.resource;

import com.mpusinhol.imageservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/image")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ImageResource {

    private final ImageService imageService;

    @GetMapping("/show/{pre-defined-type}/{seo-name}")
    public ResponseEntity<Byte[]> findImage(
            @PathVariable(name = "pre-defined-type") String preDefinedType,
            @PathVariable(name = "seo-name", required = false) String seoName,
            @RequestParam(name = "reference") String filename) {

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/flush/{pre-defined-type}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable(name = "pre-defined-type") String preDefinedType,
            @RequestParam(name = "reference") String filename) {

        return ResponseEntity.noContent().build();
    }
}
