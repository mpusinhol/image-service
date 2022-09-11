package com.mpusinhol.imageservice.service;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;
import com.mpusinhol.imageservice.exception.InternalServerErrorException;
import com.mpusinhol.imageservice.exception.InvalidFilenameException;
import com.mpusinhol.imageservice.exception.ObjectNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@Profile("test")
public class BucketServiceTest {

    @Value("${aws.s3.image-bucket}")
    private String bucket;

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Something went wrong.";

    @Autowired
    private BucketService bucketService;

    @Autowired
    private S3Client s3Client;

    @Nested
    @DisplayName("Get object test")
    class GetObjectTests {
        @Test
        @DisplayName("Happy flow")
        void happyFlow() {
            String key = "dummyKey.png";
            byte[] byteArray = new byte[] {0, 0, 0, 0, 0};
            GetObjectResponse getObjectResponse = GetObjectResponse.builder().build();
            ResponseBytes<GetObjectResponse> responseBytes = ResponseBytes.fromByteArray(getObjectResponse, byteArray);

            when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);

            assertDoesNotThrow(() -> {
                byte[] response = bucketService.getObject(key);
                assertArrayEquals(byteArray, response);
            });
        }

        @Test
        @DisplayName("No such key exception")
        void objectNotFound() {
            String key = "dummyKey.png";

            when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.class);

            assertThrows(
                    ObjectNotFoundException.class,
                    () -> bucketService.getObject(key),
                    "Could not find object with key " + key);
        }

        @Test
        @DisplayName("S3 exception - status 404")
        void s3Exception404() {
            String key = "dummyKey.png";
            AwsServiceException awsServiceException = S3Exception.builder().statusCode(404).build();

            when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenThrow(awsServiceException);

            assertThrows(
                    ObjectNotFoundException.class,
                    () -> bucketService.getObject(key),
                    "Could not find object with key " + key);
        }

        @Test
        @DisplayName("S3 exception - other status")
        void s3ExceptionOtherStatus() {
            String key = "dummyKey.png";
            AwsServiceException awsServiceException = S3Exception.builder().statusCode(500).build();

            when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenThrow(awsServiceException);

            assertThrows(
                    InternalServerErrorException.class,
                    () -> bucketService.getObject(key),
                    INTERNAL_SERVER_ERROR_MESSAGE);
        }
    }

    @Nested
    @DisplayName("Put object test")
    class PutObjectTests {
        @Test
        @DisplayName("Happy flow")
        void happyFlow() {
            String key = "dummyKey.png";
            String eTag = "dummyETag";
            byte[] object = new byte[] {0, 0, 0, 0, 0};
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            PutObjectResponse putObjectResponse = PutObjectResponse.builder()
                    .eTag(eTag)
                    .build();

            when(s3Client.putObject(eq(putObjectRequest), any(RequestBody.class))).thenReturn(putObjectResponse);

            assertDoesNotThrow(() -> {
                String response = bucketService.putObject(key, object);
                assertEquals(eTag, response);
            });
        }

        @Test
        @DisplayName("Exception thrown")
        void exceptionThrown() {
            String key = "dummyKey.png";
            byte[] object = new byte[] {0, 0, 0, 0, 0};
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            when(s3Client.putObject(eq(putObjectRequest), any(RequestBody.class))).thenThrow(AwsServiceException.class);

            assertThrows(
                    InternalServerErrorException.class,
                    () -> bucketService.putObject(key, object),
                    INTERNAL_SERVER_ERROR_MESSAGE);
        }
    }

    @Nested
    @DisplayName("Delete object test")
    class DeleteObjectTests {
        @Test
        @DisplayName("Happy flow")
        void happyFlow() {
            String key = "dummyKey.png";
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            DeleteObjectResponse deleteObjectResponse = DeleteObjectResponse.builder()
                    .build();

            when(s3Client.deleteObject(deleteObjectRequest)).thenReturn(deleteObjectResponse);

            assertDoesNotThrow(() -> bucketService.deleteObject(key));
        }

        @Test
        @DisplayName("Exception thrown")
        void exceptionThrown() {
            String key = "dummyKey.png";
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            when(s3Client.deleteObject(deleteObjectRequest)).thenThrow(SdkClientException.class);

            assertThrows(
                    InternalServerErrorException.class,
                    () -> bucketService.deleteObject(key),
                    INTERNAL_SERVER_ERROR_MESSAGE
            );
        }
    }

    @Nested
    @DisplayName("Create bucket test")
    class CreateBucketTests {
        @Test
        @DisplayName("Happy flow")
        void happyFlow() {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucket)
                    .build();
            CreateBucketResponse createBucketResponse = CreateBucketResponse.builder()
                    .build();

            when(s3Client.createBucket(createBucketRequest)).thenReturn(createBucketResponse);

            assertDoesNotThrow(() -> bucketService.createBucket(bucket));
        }

        @Test
        @DisplayName("Exception thrown")
        void exceptionThrown() {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucket)
                    .build();
            CreateBucketResponse createBucketResponse = CreateBucketResponse.builder()
                    .build();

            when(s3Client.createBucket(createBucketRequest)).thenThrow(AwsServiceException.class);

            assertThrows(
                    InternalServerErrorException.class,
                    () -> bucketService.createBucket(bucket),
                    INTERNAL_SERVER_ERROR_MESSAGE
            );
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "filenames.csv", delimiter = ';', numLinesToSkip = 1)
    @DisplayName("Generate path based on filename test")
    void testGeneratePath(String preDefinedType, String filename, boolean isValid, String expectedResponse, String expectedError) {
        ImagePreDefinedType imagePreDefinedType = ImagePreDefinedType.valueOf(preDefinedType.toUpperCase());

        if (isValid) {
            String response = bucketService.generateKeyPath(imagePreDefinedType, filename);
            assertEquals(expectedResponse, response);
        } else {
            assertThrows(
                    InvalidFilenameException.class,
                    () -> bucketService.generateKeyPath(imagePreDefinedType, filename),
                    expectedError);
        }
    }
}
