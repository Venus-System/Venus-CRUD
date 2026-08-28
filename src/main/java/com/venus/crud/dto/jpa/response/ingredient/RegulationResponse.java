package com.venus.crud.dto.jpa.response.ingredient;

import com.venus.crud.entity.enums.RegulationStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record RegulationResponse(
        Long id,
        String title,
        String country,
        String agency,
        String documentUrl,
        RegulationStatus status,
        LocalDate effectiveDate,
        String summary,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
