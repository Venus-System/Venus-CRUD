package com.venus.crud.dto.jpa.patch.ingredient;

import com.venus.crud.entity.enums.RegulationStatus;
import java.time.LocalDate;

public record RegulationPatchRequest(
        String title,
        String country,
        String agency,
        String documentUrl,
        RegulationStatus status,
        LocalDate effectiveDate,
        String summary
) {
}
