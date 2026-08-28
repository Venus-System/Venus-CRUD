package com.venus.crud.dto.jpa.request.admin;

import com.venus.crud.entity.enums.AdminRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminUserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull AdminRole role,
        @NotNull Boolean isActive
) {
}