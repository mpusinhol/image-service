package com.mpusinhol.imageservice.validation.validator;

import com.mpusinhol.imageservice.enumeration.ImagePreDefinedType;
import com.mpusinhol.imageservice.exception.InvalidImagePreDefinedTypeException;
import com.mpusinhol.imageservice.validation.ValidImagePreDefinedType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class ImagePreDefinedTypeValidator implements ConstraintValidator<ValidImagePreDefinedType, String> {

    public static final String MESSAGE = "Image pre-defined type must be one of " + Arrays.toString(ImagePreDefinedType.values());

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            ImagePreDefinedType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidImagePreDefinedTypeException(MESSAGE);
        }

        return true;
    }
}
