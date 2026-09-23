package com.venus.crud.dto.jpa.patch.user;

import com.venus.crud.entity.enums.AllergyType;
import io.swagger.v3.oas.annotations.media.Schema;

public record AllergyPatchRequest(
        @Schema(description = "Nome da alergia.", example = "Alergia a níquel")
        String allergyName,
        @Schema(description = "Natureza da alergia.")
        AllergyType allergyType
) {
}
