package com.mpusinhol.imageservice.service;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;

import java.util.regex.Pattern;

public interface BucketService {

    Pattern FILENAME_PATTERN = Pattern.compile("[^\\s]+(\\.(?i)(jpg|jpeg|tiff|png|gif|bmp))$");

    byte[] getObject(String key);

    String putObject(String key, byte[] object);

    void deleteObject(String key);

    void createBucket(String name);

    String generateKeyPath(ImagePreDefinedType imagePreDefinedType, String filename);
}
