package com.venus.crud.dto.jpa.response.user;

import com.venus.crud.entity.enums.*;
import com.venus.crud.entity.enums.HairPattern;

import java.time.OffsetDateTime;

public record UserProfileResponse(
        Long id,
        Long userId,
        SkinType skinType,
        SkinPhototype skinPhototype,
        Boolean hasHyperpigmentation,
        Boolean hasMelasma,
        Boolean hasRosacea,
        Boolean hasEczema,
        HairPattern hairPattern,
        ScalpType scalpType,
        SensitivityLevel skinSensitivity,
        Boolean acneProne,
        AgeRange ageRange,
        Gender gender,
        Boolean isPregnant,
        Boolean isBreastfeeding,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
