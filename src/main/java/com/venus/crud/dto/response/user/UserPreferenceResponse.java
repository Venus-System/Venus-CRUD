package com.venus.crud.dto.response.user;

import java.time.OffsetDateTime;

public record UserPreferenceResponse(
        Long id,
        Long userId,
        Boolean preferCrueltyFree,
        Boolean preferVegan,
        Boolean preferSustainable,
        Boolean preferFragranceFree,
        Boolean preferParabenFree,
        Boolean preferSulfateFree,
        Boolean preferSiliconeFree,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
