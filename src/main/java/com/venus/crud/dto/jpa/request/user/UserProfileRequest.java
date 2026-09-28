package com.venus.crud.dto.jpa.request.user;

import com.venus.crud.entity.enums.AgeRange;
import com.venus.crud.entity.enums.Gender;
import com.venus.crud.entity.enums.HairPattern;
import com.venus.crud.entity.enums.ScalpType;
import com.venus.crud.entity.enums.SensitivityLevel;
import com.venus.crud.entity.enums.SkinPhototype;
import com.venus.crud.entity.enums.SkinType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserProfileRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        @NotNull Long userId,
        @Schema(description = "Tipo de pele. Nulo quando o usuário não respondeu.", nullable = true)
        SkinType skinType,
        @Schema(description = "Fototipo de pele na escala Fitzpatrick. Nulo quando o usuário não respondeu.", nullable = true)
        SkinPhototype skinPhototype,
        @Schema(description = "Indica se o usuário relatou hiperpigmentação. Nulo quando o usuário não informou ou revogou o consentimento.", example = "false", nullable = true)
        Boolean hasHyperpigmentation,
        @Schema(description = "Indica se o usuário relatou melasma. Nulo quando o usuário não informou ou revogou o consentimento.", example = "false", nullable = true)
        Boolean hasMelasma,
        @Schema(description = "Indica se o usuário relatou rosácea. Nulo quando o usuário não informou ou revogou o consentimento.", example = "false", nullable = true)
        Boolean hasRosacea,
        @Schema(description = "Indica se o usuário relatou eczema. Nulo quando o usuário não informou ou revogou o consentimento.", example = "false", nullable = true)
        Boolean hasEczema,
        @Schema(description = "Padrão de curvatura do cabelo, de 1A a 4C. Nulo quando o usuário não respondeu.", nullable = true)
        HairPattern hairPattern,
        @Schema(description = "Tipo de couro cabeludo. Nulo quando o usuário não respondeu.", nullable = true)
        ScalpType scalpType,
        @Schema(description = "Nível de sensibilidade da pele. Nulo quando o usuário não respondeu ou revogou o consentimento.", nullable = true)
        SensitivityLevel skinSensitivity,
        @Schema(description = "Indica se a pele do usuário tem tendência a acne. Nulo quando o usuário não informou ou revogou o consentimento.", example = "true", nullable = true)
        Boolean acneProne,
        @Schema(description = "Faixa etária declarada pelo usuário. Nulo quando o usuário não respondeu.", nullable = true)
        AgeRange ageRange,
        @Schema(description = "Gênero declarado pelo usuário. Nulo quando o usuário não respondeu.", nullable = true)
        Gender gender,
        @Schema(description = "Indica se a usuária está grávida. Nulo quando o usuário não informou ou revogou o consentimento.", example = "false", nullable = true)
        Boolean isPregnant,
        @Schema(description = "Indica se a usuária está amamentando. Nulo quando o usuário não informou ou revogou o consentimento.", example = "false", nullable = true)
        Boolean isBreastfeeding
) {
}
