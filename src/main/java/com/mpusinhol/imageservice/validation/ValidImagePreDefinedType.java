package com.mpusinhol.imageservice.validation;

import com.mpusinhol.imageservice.validation.validator.ImagePreDefinedTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ImagePreDefinedTypeValidator.class)
public @interface ValidImagePreDefinedType {
    String message() default "Image pre defined type not valid"; //Only constants allowed, unfortunately
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
