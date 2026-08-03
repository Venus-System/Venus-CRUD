package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.RecommendationLevel;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RecommendationLevelConverter extends LowercaseEnumConverter<RecommendationLevel> {
    public RecommendationLevelConverter() {
        super(RecommendationLevel.class);
    }
}
