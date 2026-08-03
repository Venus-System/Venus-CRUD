package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.RoutineType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoutineTypeConverter extends LowercaseEnumConverter<RoutineType> {
    public RoutineTypeConverter() {
        super(RoutineType.class);
    }
}
