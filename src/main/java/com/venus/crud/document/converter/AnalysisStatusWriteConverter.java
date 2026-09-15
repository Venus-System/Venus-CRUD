package com.venus.crud.document.converter;

import com.venus.crud.entity.enums.AnalysisStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class AnalysisStatusWriteConverter implements Converter<AnalysisStatus, String> {

    @Override
    public String convert(AnalysisStatus source) {
        return source == null ? null : source.name().toLowerCase();
    }
}
