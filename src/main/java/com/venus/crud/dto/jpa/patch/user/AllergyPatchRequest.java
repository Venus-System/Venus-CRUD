package com.venus.crud.dto.jpa.patch.user;

import com.venus.crud.entity.enums.AllergyType;

public record AllergyPatchRequest(
        String allergyName,
        AllergyType allergyType
) {
}
