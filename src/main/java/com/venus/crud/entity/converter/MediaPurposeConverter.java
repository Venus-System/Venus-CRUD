package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.MediaPurpose;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MediaPurposeConverter extends LowercaseEnumConverter<MediaPurpose> {
    public MediaPurposeConverter() {
        super(MediaPurpose.class);
    }
}
