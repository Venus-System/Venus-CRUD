package com.venus.crud.dto.jpa.patch.product;

import com.venus.crud.entity.enums.ClaimType;

public record ClaimPatchRequest(
        String name,
        String description,
        ClaimType claimType
) {
}
