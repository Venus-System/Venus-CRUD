package com.venus.crud.dto.jpa.patch.user;

import com.venus.crud.entity.enums.AgeRange;
import com.venus.crud.entity.enums.Gender;
import com.venus.crud.entity.enums.HairPattern;
import com.venus.crud.entity.enums.ScalpType;
import com.venus.crud.entity.enums.SensitivityLevel;
import com.venus.crud.entity.enums.SkinPhototype;
import com.venus.crud.entity.enums.SkinType;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserProfilePatchRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Tipo de pele.")
        SkinType skinType,
        @Schema(description = "Fototipo de pele na escala Fitzpatrick.")
        SkinPhototype skinPhototype,
        @Schema(description = "Indica se o usuário relatou hiperpigmentação.", example = "false")
        Boolean hasHyperpigmentation,
        @Schema(description = "Indica se o usuário relatou melasma.", example = "false")
        Boolean hasMelasma,
        @Schema(description = "Indica se o usuário relatou rosácea.", example = "false")
        Boolean hasRosacea,
        @Schema(description = "Indica se o usuário relatou eczema.", example = "false")
        Boolean hasEczema,
        @Schema(description = "Padrão de curvatura do cabelo, de 1A a 4C.")
        HairPattern hairPattern,
        @Schema(description = "Tipo de couro cabeludo.")
        ScalpType scalpType,
        @Schema(description = "Nível de sensibilidade da pele.")
        SensitivityLevel skinSensitivity,
        @Schema(description = "Indica se a pele do usuário tem tendência a acne.", example = "true")
        Boolean acneProne,
        @Schema(description = "Faixa etária declarada pelo usuário.")
        AgeRange ageRange,
        @Schema(description = "Gênero declarado pelo usuário.")
        Gender gender,
        @Schema(description = "Indica se a usuária está grávida.", example = "false")
        Boolean isPregnant,
        @Schema(description = "Indica se a usuária está amamentando.", example = "false")
        Boolean isBreastfeeding
) {
}
