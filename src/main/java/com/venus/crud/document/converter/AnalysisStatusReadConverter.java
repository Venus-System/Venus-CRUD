package com.venus.crud.document.converter;

import com.venus.crud.entity.enums.AnalysisStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class AnalysisStatusReadConverter implements Converter<String, AnalysisStatus> {

    @Override
    public AnalysisStatus convert(String source) {
        return source == null ? null : AnalysisStatus.valueOf(source.toUpperCase());
    }
}
