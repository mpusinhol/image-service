package com.mpusinhol.imageservice.service;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;
import com.mpusinhol.imageservice.exception.InvalidFilenameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BucketServiceTest {

    @Autowired
    private BucketService bucketService;

    @ParameterizedTest
    @CsvFileSource(resources = "filenames.csv", delimiter = ';', numLinesToSkip = 1)
    @DisplayName("Test generate path based on filename")
    void testGeneratePath(String preDefinedType, String filename, boolean isValid, String expectedResponse, String expectedError) {
        ImagePreDefinedType imagePreDefinedType = ImagePreDefinedType.valueOf(preDefinedType.toUpperCase());

        if (isValid) {
            String response = bucketService.generatePath(imagePreDefinedType, filename);
            assertEquals(expectedResponse, response);
        } else {
            assertThrows(
                    InvalidFilenameException.class,
                    () -> bucketService.generatePath(imagePreDefinedType, filename),
                    expectedError);
        }
    }
}
