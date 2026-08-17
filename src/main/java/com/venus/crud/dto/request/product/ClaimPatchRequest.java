package com.venus.crud.dto.request.product;

import com.venus.crud.entity.enums.ClaimType;

public record ClaimPatchRequest(
        String name,
        String description,
        ClaimType claimType
) {
}
