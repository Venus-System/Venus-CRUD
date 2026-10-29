package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.VoteType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VoteTypeConverter extends LowercaseEnumConverter<VoteType> {
    public VoteTypeConverter() {
        super(VoteType.class);
    }
}
