package com.venus.crud.config;

import com.venus.crud.entity.enums.MediaDeliveryType;
import com.venus.crud.entity.enums.MediaPurpose;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "venus.media")
public record MediaProperties(
        List<String> allowedImageTypes,
        MediaDeliveryType defaultDeliveryType,
        String defaultResourceType,
        Limits avatar,
        Limits product
) {

    public record Limits(long maxBytes, int maxWidth, int maxHeight) {
    }

    public Limits limitsFor(MediaPurpose purpose) {
        return purpose == MediaPurpose.AVATAR ? avatar : product;
    }
}
