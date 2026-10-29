package com.venus.crud.document.converter;

import com.venus.crud.entity.enums.IngredientDecisionAction;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class IngredientDecisionActionWriteConverter implements Converter<IngredientDecisionAction, String> {

    @Override
    public String convert(IngredientDecisionAction source) {
        return source == null ? null : source.name().toLowerCase();
    }
}
