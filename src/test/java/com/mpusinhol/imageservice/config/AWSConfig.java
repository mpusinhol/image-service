package com.mpusinhol.imageservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestConfiguration
public class AWSConfig {

    @MockBean
    private S3Client s3Client;

    @BeforeEach
    void setup() {
        when(s3Client.createBucket(any(CreateBucketRequest.class))).thenReturn(CreateBucketResponse.builder().build());
    }
}
