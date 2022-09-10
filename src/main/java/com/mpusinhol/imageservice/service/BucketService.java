package com.mpusinhol.imageservice.service;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;

import java.io.InputStream;
import java.util.regex.Pattern;

public interface BucketService {

    Pattern FILENAME_PATTERN = Pattern.compile("[^\\s]+(\\.(?i)(jpg|jpeg|tiff|png|gif|bmp))$");

    InputStream getObject(String path);

    void putObject(String path, InputStream object);

    String generatePath(ImagePreDefinedType imagePreDefinedType, String filename);
}
