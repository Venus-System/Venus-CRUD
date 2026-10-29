package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.MediaStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MediaStatusConverter extends LowercaseEnumConverter<MediaStatus> {
    public MediaStatusConverter() {
        super(MediaStatus.class);
    }
}
