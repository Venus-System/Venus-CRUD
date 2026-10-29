package com.venus.crud.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.venus.crud.entity.enums.MediaDeliveryType;
import com.venus.crud.entity.enums.MediaPurpose;
import java.util.List;
import org.junit.jupiter.api.Test;

class MediaPropertiesTest {

    private static final MediaProperties.Limits AVATAR_LIMITS = new MediaProperties.Limits(2_000_000L, 1024, 1024);
    private static final MediaProperties.Limits PRODUCT_LIMITS = new MediaProperties.Limits(5_000_000L, 4096, 4096);

    private final MediaProperties properties = new MediaProperties(List.of("image/jpeg"), MediaDeliveryType.UPLOAD,
            "image", AVATAR_LIMITS, PRODUCT_LIMITS);

    @Test
    void listCoverUsesTheAvatarLimits() {
        assertThat(properties.limitsFor(MediaPurpose.LIST_COVER)).isSameAs(AVATAR_LIMITS);
    }

    @Test
    void avatarUsesTheAvatarLimits() {
        assertThat(properties.limitsFor(MediaPurpose.AVATAR)).isSameAs(AVATAR_LIMITS);
    }

    @Test
    void productPhotoUsesTheProductLimits() {
        assertThat(properties.limitsFor(MediaPurpose.PRODUCT_PHOTO)).isSameAs(PRODUCT_LIMITS);
    }
}
