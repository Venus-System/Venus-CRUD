package com.venus.crud.dto.request.user;

import com.venus.crud.entity.enums.ListType;

public record UserListPatchRequest(
        Long userId,
        String name,
        ListType listType
) {
}
