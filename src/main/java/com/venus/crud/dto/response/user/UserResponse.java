package com.venus.crud.dto.response.user;

import com.venus.crud.entity.enums.UserStatus;
import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String firebaseUid,
        String name,
        String email,
        UserStatus status,
        OffsetDateTime lastLogin,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
