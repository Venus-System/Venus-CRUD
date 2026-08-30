package com.venus.crud.dto.jpa.patch.user;

import com.venus.crud.entity.enums.ListType;

public record UserListPatchRequest(
        Long userId,
        String name,
        ListType listType
) {
}
