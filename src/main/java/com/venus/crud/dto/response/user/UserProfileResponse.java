package com.venus.crud.dto.response.user;

import com.venus.crud.entity.enums.AgeRange;
import com.venus.crud.entity.enums.Gender;
import com.venus.crud.entity.enums.HairType;
import com.venus.crud.entity.enums.ScalpType;
import com.venus.crud.entity.enums.SensitivityLevel;
import com.venus.crud.entity.enums.SkinPhototype;
import com.venus.crud.entity.enums.SkinType;
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
        HairType hairType,
        ScalpType scalpType,
        SensitivityLevel skinSensitivity,
        Boolean acneProne,
        AgeRange ageRange,
        Gender gender,
        Boolean isPregnant,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
