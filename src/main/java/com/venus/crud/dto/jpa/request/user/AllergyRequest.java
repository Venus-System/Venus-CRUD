package com.venus.crud.dto.jpa.request.user;

import com.venus.crud.entity.enums.AllergyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AllergyRequest(
        @Schema(description = "Nome da alergia.", example = "Alergia a níquel")
        @NotBlank String allergyName,
        @Schema(description = "Natureza da alergia.")
        @NotNull AllergyType allergyType
) {
}
