package com.venus.crud.dto.jpa.response.user;

import com.venus.crud.entity.enums.AllergyType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record AllergyResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Nome da alergia.", example = "Alergia a níquel")
        String allergyName,
        @Schema(description = "Natureza da alergia.")
        AllergyType allergyType,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
