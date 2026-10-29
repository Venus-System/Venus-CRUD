package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.user.AllergyResponse;
import com.venus.crud.entity.enums.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserAllergyDetailResponse(
        @Schema(description = "Alergia a que o registro se refere.")
        AllergyResponse allergy,
        @Schema(description = "Gravidade do alerta gerado pela regra.")
        RiskLevel severity
) {
}