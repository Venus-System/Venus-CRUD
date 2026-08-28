package com.venus.crud.dto.jpa.patch.user;

import com.venus.crud.entity.enums.UserStatus;
import jakarta.validation.constraints.Email;
import java.time.OffsetDateTime;

public record UserPatchRequest(
        String name,
        @Email String email,
        String password,
        UserStatus status,
        OffsetDateTime lastLogin
) {
}
