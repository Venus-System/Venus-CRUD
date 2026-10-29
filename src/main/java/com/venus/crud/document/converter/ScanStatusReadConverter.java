package com.venus.crud.document.converter;

import com.venus.crud.entity.enums.ScanStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class ScanStatusReadConverter implements Converter<String, ScanStatus> {

    @Override
    public ScanStatus convert(String source) {
        return source == null ? null : ScanStatus.valueOf(source.toUpperCase());
    }
}
