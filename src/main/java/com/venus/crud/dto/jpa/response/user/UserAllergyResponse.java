package com.venus.crud.dto.jpa.response.user;

import com.venus.crud.entity.enums.RiskLevel;
import java.time.OffsetDateTime;

public record UserAllergyResponse(
        Long id,
        Long userId,
        Long allergyId,
        RiskLevel severity,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
