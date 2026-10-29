package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.ClaimType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ClaimTypeConverter extends LowercaseEnumConverter<ClaimType> {
    public ClaimTypeConverter() {
        super(ClaimType.class);
    }
}
