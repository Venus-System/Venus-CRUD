package com.venus.crud.document.converter;

import com.venus.crud.entity.enums.ScanStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class ScanStatusWriteConverter implements Converter<ScanStatus, String> {

    @Override
    public String convert(ScanStatus source) {
        return source == null ? null : source.name().toLowerCase();
    }
}
