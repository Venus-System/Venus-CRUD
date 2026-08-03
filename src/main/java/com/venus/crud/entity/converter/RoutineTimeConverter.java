package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.RoutineTime;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoutineTimeConverter extends LowercaseEnumConverter<RoutineTime> {
    public RoutineTimeConverter() {
        super(RoutineTime.class);
    }
}
