package com.venus.crud.dto.request.user;

import com.venus.crud.entity.enums.RiskLevel;
import jakarta.validation.constraints.NotNull;

public record UserAllergyRequest(
        @NotNull Long userId,
        @NotNull Long allergyId,
        @NotNull RiskLevel severity
) {
}
