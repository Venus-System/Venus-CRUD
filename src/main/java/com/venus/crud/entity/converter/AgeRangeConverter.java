package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.AgeRange;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AgeRangeConverter extends LowercaseEnumConverter<AgeRange> {
    public AgeRangeConverter() {
        super(AgeRange.class);
    }
}
