package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.MediaDeliveryType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MediaDeliveryTypeConverter extends LowercaseEnumConverter<MediaDeliveryType> {
    public MediaDeliveryTypeConverter() {
        super(MediaDeliveryType.class);
    }
}
