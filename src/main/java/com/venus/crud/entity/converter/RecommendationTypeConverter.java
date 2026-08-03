package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.RecommendationType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RecommendationTypeConverter extends LowercaseEnumConverter<RecommendationType> {
    public RecommendationTypeConverter() {
        super(RecommendationType.class);
    }
}
