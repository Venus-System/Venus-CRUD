package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.AllergyType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AllergyTypeConverter extends LowercaseEnumConverter<AllergyType> {
    public AllergyTypeConverter() {
        super(AllergyType.class);
    }
}
