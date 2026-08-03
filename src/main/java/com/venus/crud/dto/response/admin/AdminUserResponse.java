package com.venus.crud.dto.response.admin;

import com.venus.crud.entity.enums.AdminRole;
import java.time.OffsetDateTime;

public record AdminUserResponse(
        Long id,
        String name,
        String email,
        AdminRole role,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
