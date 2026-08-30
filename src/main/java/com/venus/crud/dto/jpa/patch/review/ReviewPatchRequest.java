package com.venus.crud.dto.jpa.patch.review;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record ReviewPatchRequest(
        Long userId,
        Long productVersionId,
        @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal rating,
        String title,
        String comment,
        Boolean verifiedUse
) {
}
