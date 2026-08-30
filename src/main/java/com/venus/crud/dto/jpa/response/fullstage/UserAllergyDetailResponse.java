package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.user.AllergyResponse;
import com.venus.crud.entity.enums.RiskLevel;

public record UserAllergyDetailResponse(
        AllergyResponse allergy,
        RiskLevel severity
) {
}