package com.venus.crud.document;

import com.venus.crud.entity.enums.AnalysisStatus;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "scan_sessions")
public class ScanSession {

    @Id
    private String id;

    private AnalysisStatus status;

    private ScanDevice device;

    @Field("started_at")
    private OffsetDateTime startedAt;

    @Field("finished_at")
    private OffsetDateTime finishedAt;

    @Field("quality_check")
    private ScanQualityCheck qualityCheck;

    @Field("full_ocr_text")
    private String fullOcrText;

    @Setter(AccessLevel.NONE)
    @CreatedDate
    @Field("created_at")
    private OffsetDateTime createdAt;

    @Setter(AccessLevel.NONE)
    @LastModifiedDate
    @Field("updated_at")
    private OffsetDateTime updatedAt;
}
