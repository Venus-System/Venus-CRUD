package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.ListCoverKey;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ListCoverKeyConverter extends LowercaseEnumConverter<ListCoverKey> {
    public ListCoverKeyConverter() {
        super(ListCoverKey.class);
    }
}
