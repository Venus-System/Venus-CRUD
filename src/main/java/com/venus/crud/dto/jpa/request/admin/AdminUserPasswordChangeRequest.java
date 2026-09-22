package com.venus.crud.dto.jpa.request.admin;

import jakarta.validation.constraints.NotBlank;

public record AdminUserPasswordChangeRequest(
        @NotBlank String currentPassword,
        @NotBlank String newPassword
) {
}
