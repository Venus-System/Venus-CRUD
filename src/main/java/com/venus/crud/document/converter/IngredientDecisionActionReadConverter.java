package com.venus.crud.document.converter;

import com.venus.crud.entity.enums.IngredientDecisionAction;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class IngredientDecisionActionReadConverter implements Converter<String, IngredientDecisionAction> {

    @Override
    public IngredientDecisionAction convert(String source) {
        return source == null ? null : IngredientDecisionAction.valueOf(source.toUpperCase());
    }
}
