package com.venus.crud.dto.jpa.request.scoring;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ScoreCategoryRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull BigDecimal defaultWeight
) {
}
