package com.venus.crud.dto.jpa.response.user;

import com.venus.crud.entity.enums.ListType;
import java.time.OffsetDateTime;

public record UserListResponse(
        Long id,
        Long userId,
        String name,
        ListType listType,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
