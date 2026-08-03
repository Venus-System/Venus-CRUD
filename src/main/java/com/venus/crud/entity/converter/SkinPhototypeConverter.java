package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.SkinPhototype;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SkinPhototypeConverter extends LowercaseEnumConverter<SkinPhototype> {
    public SkinPhototypeConverter() {
        super(SkinPhototype.class);
    }
}
