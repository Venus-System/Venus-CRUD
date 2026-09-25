package com.venus.crud.dto.jpa.patch.user;

import com.venus.crud.entity.enums.AgeRange;
import com.venus.crud.entity.enums.Gender;
import com.venus.crud.entity.enums.HairPattern;
import com.venus.crud.entity.enums.ScalpType;
import com.venus.crud.entity.enums.SensitivityLevel;
import com.venus.crud.entity.enums.SkinPhototype;
import com.venus.crud.entity.enums.SkinType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.openapitools.jackson.nullable.JsonNullable;

public record UserProfilePatchRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Tipo de pele.")
        SkinType skinType,
        @Schema(description = "Fototipo de pele na escala Fitzpatrick.")
        SkinPhototype skinPhototype,
        @Schema(description = "Indica se o usuário relatou hiperpigmentação. Enviar null apaga o valor; omitir o campo mantém o atual.", example = "false",
                implementation = Boolean.class, nullable = true)
        JsonNullable<Boolean> hasHyperpigmentation,
        @Schema(description = "Indica se o usuário relatou melasma. Enviar null apaga o valor; omitir o campo mantém o atual.", example = "false",
                implementation = Boolean.class, nullable = true)
        JsonNullable<Boolean> hasMelasma,
        @Schema(description = "Indica se o usuário relatou rosácea. Enviar null apaga o valor; omitir o campo mantém o atual.", example = "false",
                implementation = Boolean.class, nullable = true)
        JsonNullable<Boolean> hasRosacea,
        @Schema(description = "Indica se o usuário relatou eczema. Enviar null apaga o valor; omitir o campo mantém o atual.", example = "false",
                implementation = Boolean.class, nullable = true)
        JsonNullable<Boolean> hasEczema,
        @Schema(description = "Padrão de curvatura do cabelo, de 1A a 4C.")
        HairPattern hairPattern,
        @Schema(description = "Tipo de couro cabeludo.")
        ScalpType scalpType,
        @Schema(description = "Nível de sensibilidade da pele.")
        SensitivityLevel skinSensitivity,
        @Schema(description = "Indica se a pele do usuário tem tendência a acne. Enviar null apaga o valor; omitir o campo mantém o atual.", example = "true",
                implementation = Boolean.class, nullable = true)
        JsonNullable<Boolean> acneProne,
        @Schema(description = "Faixa etária declarada pelo usuário.")
        AgeRange ageRange,
        @Schema(description = "Gênero declarado pelo usuário.")
        Gender gender,
        @Schema(description = "Indica se a usuária está grávida. Enviar null apaga o valor; omitir o campo mantém o atual.", example = "false",
                implementation = Boolean.class, nullable = true)
        JsonNullable<Boolean> isPregnant,
        @Schema(description = "Indica se a usuária está amamentando. Enviar null apaga o valor; omitir o campo mantém o atual.", example = "false",
                implementation = Boolean.class, nullable = true)
        JsonNullable<Boolean> isBreastfeeding
) {
}
