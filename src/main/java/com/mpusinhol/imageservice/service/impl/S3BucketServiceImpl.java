package com.mpusinhol.imageservice.service.impl;

import com.mpusinhol.imageservice.service.BucketService;

import java.io.InputStream;

public class S3BucketServiceImpl implements BucketService {
    @Override
    public InputStream getObject(String path) {
        return null;
    }

    @Override
    public void putObject(String path, InputStream object) {

    }
}
