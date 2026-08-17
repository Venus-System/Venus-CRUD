package com.venus.crud.dto.request.user;

import com.venus.crud.entity.enums.RiskLevel;

public record UserAllergyPatchRequest(
        Long userId,
        Long allergyId,
        RiskLevel severity
) {
}
