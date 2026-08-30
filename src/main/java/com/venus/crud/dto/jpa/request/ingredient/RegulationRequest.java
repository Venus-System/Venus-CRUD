package com.venus.crud.dto.jpa.request.ingredient;

import com.venus.crud.entity.enums.RegulationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegulationRequest(
        @NotBlank String title,
        @NotBlank String country,
        @NotBlank String agency,
        @NotBlank String documentUrl,
        @NotNull RegulationStatus status,
        LocalDate effectiveDate,
        @NotBlank String summary
) {
}
