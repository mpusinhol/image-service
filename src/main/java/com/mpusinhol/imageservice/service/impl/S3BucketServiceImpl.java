package com.mpusinhol.imageservice.service.impl;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;
import com.mpusinhol.imageservice.exception.InvalidFilenameException;
import com.mpusinhol.imageservice.service.BucketService;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class S3BucketServiceImpl implements BucketService {
    @Override
    public InputStream getObject(String path) {
        return null;
    }

    @Override
    public void putObject(String path, InputStream object) {

    }

    @Override
    public String generatePath(ImagePreDefinedType imagePreDefinedType, String filename) {
        if (!FILENAME_PATTERN.matcher(filename).matches()) {
            throw new InvalidFilenameException(String.format("Invalid filename %s", filename));
        }

        String nameWithoutExtension = filename.substring(0, filename.lastIndexOf("."));
        int nameLengthWithoutExtension = nameWithoutExtension.length();

        String path = String.format("/%s", imagePreDefinedType.toString().toLowerCase());

        if (nameLengthWithoutExtension > 4) {
            path = String.format("%s/%s", path, nameWithoutExtension.substring(0, 4));

            if (nameLengthWithoutExtension > 8) {
                path = String.format("%s/%s/%s", path, nameWithoutExtension.substring(4, 8), filename);
            } else {
                path = String.format("%s/%s", path, filename);
            }
        } else {
            path = String.format("%s/%s", path, filename);
        }

        return path;
    }
}
