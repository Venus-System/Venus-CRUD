package com.venus.crud.dto.request.user;

import com.venus.crud.entity.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record UserRequest(
        @NotBlank String firebaseUid,
        @NotBlank String name,
        @NotNull UserStatus status,
        OffsetDateTime lastLogin
) {
}
