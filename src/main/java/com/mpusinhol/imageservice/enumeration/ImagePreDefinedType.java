package com.mpusinhol.imageservice.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImagePreDefinedType {
    THUMBNAIL(255, 382, 90, ImageScaleType.CROP, "#000000", ImageType.JPG, "ecom-thumb-base"),
    DETAIL_LARGE(355, 563, 70, ImageScaleType.CROP, "#000000", ImageType.PNG, "ecom-detail-large-base");

    private Integer height;
    private Integer width;
    private Integer quality;
    private ImageScaleType scaleType;
    private String fillColor;
    private ImageType imageType;
    private String sourceName;
}
