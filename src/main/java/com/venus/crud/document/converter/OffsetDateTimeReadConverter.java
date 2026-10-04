package com.venus.crud.document.converter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class OffsetDateTimeReadConverter implements Converter<Date, OffsetDateTime> {

    @Override
    public OffsetDateTime convert(Date source) {
        return source == null ? null : source.toInstant().atOffset(ZoneOffset.UTC);
    }
}
