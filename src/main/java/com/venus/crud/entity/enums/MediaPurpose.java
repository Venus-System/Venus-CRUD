package com.venus.crud.entity.enums;

public enum MediaPurpose {
    AVATAR(true),
    PRODUCT_PHOTO(false),
    LIST_COVER(true);

    private final boolean imageOfUser;

    MediaPurpose(boolean imageOfUser) {
        this.imageOfUser = imageOfUser;
    }

    public boolean isImageOfUser() {
        return imageOfUser;
    }
}
