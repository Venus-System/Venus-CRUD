package com.venus.crud.dto.response.user;

import com.venus.crud.entity.enums.AllergyType;
import java.time.OffsetDateTime;

public record AllergyResponse(
        Long id,
        String allergyName,
        AllergyType allergyType,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
