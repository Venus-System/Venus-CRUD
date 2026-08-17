package com.venus.crud.dto.request.user;

import com.venus.crud.entity.enums.AllergyType;

public record AllergyPatchRequest(
        String allergyName,
        AllergyType allergyType
) {
}
