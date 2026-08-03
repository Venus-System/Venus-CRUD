package com.venus.crud.dto.request.user;

import com.venus.crud.entity.enums.AllergyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AllergyRequest(
        @NotBlank String allergyName,
        @NotNull AllergyType allergyType
) {
}
