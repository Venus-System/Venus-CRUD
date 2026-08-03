package com.venus.crud.dto.request.user;

import com.venus.crud.entity.enums.AgeRange;
import com.venus.crud.entity.enums.Gender;
import com.venus.crud.entity.enums.HairType;
import com.venus.crud.entity.enums.ScalpType;
import com.venus.crud.entity.enums.SensitivityLevel;
import com.venus.crud.entity.enums.SkinPhototype;
import com.venus.crud.entity.enums.SkinType;
import jakarta.validation.constraints.NotNull;

public record UserProfileRequest(
        @NotNull Long userId,
        @NotNull SkinType skinType,
        @NotNull SkinPhototype skinPhototype,
        @NotNull Boolean hasHyperpigmentation,
        @NotNull Boolean hasMelasma,
        @NotNull Boolean hasRosacea,
        @NotNull Boolean hasEczema,
        @NotNull HairType hairType,
        @NotNull ScalpType scalpType,
        @NotNull SensitivityLevel skinSensitivity,
        @NotNull Boolean acneProne,
        @NotNull AgeRange ageRange,
        @NotNull Gender gender,
        @NotNull Boolean isPregnant
) {
}
