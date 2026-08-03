package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.RestrictionType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RestrictionTypeConverter extends LowercaseEnumConverter<RestrictionType> {
    public RestrictionTypeConverter() {
        super(RestrictionType.class);
    }
}
