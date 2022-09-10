package com.mpusinhol.imageservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record StandardError(
        Integer status,
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant timestamp) {
}
