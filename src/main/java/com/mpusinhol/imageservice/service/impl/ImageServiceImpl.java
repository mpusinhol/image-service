package com.mpusinhol.imageservice.service.impl;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;
import com.mpusinhol.imageservice.service.ImageService;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

    @Override
    public Byte[] findImage(ImagePreDefinedType imagePreDefinedType, String seoName, String filename) {
        return new Byte[0];
    }

    @Override
    public void deleteImage(ImagePreDefinedType imagePreDefinedType, String filename) {

    }
}
