package com.venus.crud.document.converter;

import com.venus.crud.entity.enums.IngredientMatchStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class IngredientMatchStatusWriteConverter implements Converter<IngredientMatchStatus, String> {

    @Override
    public String convert(IngredientMatchStatus source) {
        return source == null ? null : source.name().toLowerCase();
    }
}
