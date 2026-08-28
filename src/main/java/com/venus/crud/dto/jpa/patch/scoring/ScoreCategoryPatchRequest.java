package com.venus.crud.dto.jpa.patch.scoring;

import java.math.BigDecimal;

public record ScoreCategoryPatchRequest(
        String name,
        String description,
        BigDecimal defaultWeight
) {
}
