package com.venus.crud.config;

import com.venus.crud.entity.enums.MediaPurpose;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudinary")
public record CloudinaryProperties(
        String cloudName,
        Account users,
        Account products
) {

    public record Account(String apiKey, String apiSecret, String uploadPreset) {
    }

    public Account accountFor(MediaPurpose purpose) {
        return purpose == MediaPurpose.AVATAR ? users : products;
    }
}
