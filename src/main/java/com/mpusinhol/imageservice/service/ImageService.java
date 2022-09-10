package com.mpusinhol.imageservice.service;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;

public interface ImageService {

    Byte[] findImage(ImagePreDefinedType imagePreDefinedType, String seoName, String filename);

    void deleteImage(ImagePreDefinedType imagePreDefinedType, String filename);
}
