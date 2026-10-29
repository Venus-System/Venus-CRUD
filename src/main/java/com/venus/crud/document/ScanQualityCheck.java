package com.venus.crud.document;

import com.venus.crud.entity.enums.AnalysisStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ScanQualityCheck {

    @Field("blur_score")
    private Double blurScore;

    private Double brightness;

    @Field("background_ok")
    private Boolean backgroundOk;

    private AnalysisStatus status;
}
