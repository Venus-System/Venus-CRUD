package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.ReviewStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReviewStatusConverter extends LowercaseEnumConverter<ReviewStatus> {
    public ReviewStatusConverter() {
        super(ReviewStatus.class);
    }
}
