package com.venus.crud.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.venus.crud.entity.enums.MediaPurpose;
import org.junit.jupiter.api.Test;

class CloudinaryPropertiesTest {

    private static final CloudinaryProperties.Account USERS =
            new CloudinaryProperties.Account("chave-usuarios", "segredo-usuarios", "preset-usuarios");
    private static final CloudinaryProperties.Account PRODUCTS =
            new CloudinaryProperties.Account("chave-produtos", "segredo-produtos", "preset-produtos");

    private final CloudinaryProperties properties = new CloudinaryProperties("venus", USERS, PRODUCTS);

    @Test
    void listCoverGoesToTheUsersAccount() {
        assertThat(properties.accountFor(MediaPurpose.LIST_COVER)).isSameAs(USERS);
    }

    @Test
    void avatarGoesToTheUsersAccount() {
        assertThat(properties.accountFor(MediaPurpose.AVATAR)).isSameAs(USERS);
    }

    @Test
    void productPhotoGoesToTheProductsAccount() {
        assertThat(properties.accountFor(MediaPurpose.PRODUCT_PHOTO)).isSameAs(PRODUCTS);
    }
}
