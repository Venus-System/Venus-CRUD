package com.venus.crud.dto.jpa.request.review;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ReviewRequest(
        @NotNull Long userId,
        @NotNull Long productVersionId,
        @NotNull @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal rating,
        @NotBlank String title,
        @NotBlank String comment,
        @NotNull Boolean verifiedUse
) {
}
