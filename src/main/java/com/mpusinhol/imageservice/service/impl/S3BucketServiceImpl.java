package com.mpusinhol.imageservice.service.impl;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;
import com.mpusinhol.imageservice.exception.InternalServerErrorException;
import com.mpusinhol.imageservice.exception.InvalidFilenameException;
import com.mpusinhol.imageservice.exception.ObjectNotFoundException;
import com.mpusinhol.imageservice.service.BucketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3BucketServiceImpl implements BucketService {

    private final S3Client s3Client;

    @Value("${aws.s3.image-bucket}")
    private String bucket;

    @PostConstruct
    private void createBucketOnStart() {
        createBucket(bucket);
    }

    @Override
    public byte[] getObject(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .key(key)
                .bucket(bucket)
                .build();
        try {
            return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
        } catch (NoSuchKeyException e) {
            throw new ObjectNotFoundException(String.format("Could not find object with key %s", key));
        } catch (S3Exception e) {
            if (e.statusCode() == HttpStatus.NOT_FOUND.value()) {
                throw new ObjectNotFoundException(String.format("Could not find object with key %s", key));
            }
            log.error("Error while fetching object {}", key, e);
            throw new InternalServerErrorException("Something went wrong.", e);
        }
    }

    @Override
    public String putObject(String key, byte[] object) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try {
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(object));
            return putObjectResponse.eTag();
        } catch (AwsServiceException | SdkClientException e) {
            log.error("Error while putting object in the bucket. Key {}; error: {}", key, e.getMessage(), e);
            throw new InternalServerErrorException("Something went wrong.", e);
        }
    }

    @Override
    public void deleteObject(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .key(key)
                .bucket(bucket)
                .build();

        try {
            s3Client.deleteObject(deleteObjectRequest);
        } catch (AwsServiceException | SdkClientException e) {
            log.error("Error while deleting object from the bucket. Key {}; error: {}", key, e.getMessage(), e);
            throw new InternalServerErrorException("Something went wrong.", e);
        }
    }

    @Override
    public void createBucket(String name) {
        try {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(name)
                    .build();

            s3Client.createBucket(createBucketRequest);
        } catch (AwsServiceException | SdkClientException e) {
            String message = String.format("Unable to create bucket %s.", name);
            log.error(message, e);
            throw new InternalServerErrorException(message, e);
        }
    }

    @Override
    public String generateKeyPath(ImagePreDefinedType imagePreDefinedType, String filename) {
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
