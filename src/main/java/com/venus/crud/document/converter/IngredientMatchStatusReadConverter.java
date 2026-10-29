package com.venus.crud.document.converter;

import com.venus.crud.entity.enums.IngredientMatchStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class IngredientMatchStatusReadConverter implements Converter<String, IngredientMatchStatus> {

    @Override
    public IngredientMatchStatus convert(String source) {
        return source == null ? null : IngredientMatchStatus.valueOf(source.toUpperCase());
    }
}
