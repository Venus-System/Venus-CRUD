package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.ListType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ListTypeConverter extends LowercaseEnumConverter<ListType> {
    public ListTypeConverter() {
        super(ListType.class);
    }
}
