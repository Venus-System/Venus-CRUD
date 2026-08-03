package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.EffectCategory;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EffectCategoryConverter extends LowercaseEnumConverter<EffectCategory> {
    public EffectCategoryConverter() {
        super(EffectCategory.class);
    }
}
