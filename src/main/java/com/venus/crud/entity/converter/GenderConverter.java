package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.Gender;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderConverter extends LowercaseEnumConverter<Gender> {
    public GenderConverter() {
        super(Gender.class);
    }
}
