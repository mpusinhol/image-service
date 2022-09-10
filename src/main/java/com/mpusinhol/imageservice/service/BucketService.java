package com.mpusinhol.imageservice.service;

import java.io.InputStream;

public interface BucketService {

    InputStream getObject(String path);

    void putObject(String path, InputStream object);
}
