package com.venus.crud.dto.request.scoring;

import java.math.BigDecimal;

public record ScoreCategoryPatchRequest(
        String name,
        String description,
        BigDecimal defaultWeight
) {
}
