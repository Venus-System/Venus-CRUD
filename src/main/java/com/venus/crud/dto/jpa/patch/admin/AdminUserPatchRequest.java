package com.venus.crud.dto.jpa.patch.admin;

import com.venus.crud.entity.enums.AdminRole;
import jakarta.validation.constraints.Email;

public record AdminUserPatchRequest(
        String name,
        @Email String email,
        AdminRole role,
        Boolean isActive
) {
}
