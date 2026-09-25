package com.venus.crud.document;

import com.venus.crud.entity.enums.ScanStatus;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
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

    @Field("scan_id")
    private String scanId;

    private ScanStatus status;

    @Setter(AccessLevel.NONE)
    @Version
    private Long version;

    private ScanSource source;

    private ScanDevice device;

    @Field("started_at")
    private OffsetDateTime startedAt;

    @Field("finished_at")
    private OffsetDateTime finishedAt;

    @Field("quality_check")
    private ScanQualityCheck qualityCheck;

    private ScanImages images;

    private ScanOcr ocr;

    private List<ScanIngredient> ingredients;

    @Setter(AccessLevel.NONE)
    @CreatedDate
    @Field("created_at")
    private OffsetDateTime createdAt;

    @Setter(AccessLevel.NONE)
    @LastModifiedDate
    @Field("updated_at")
    private OffsetDateTime updatedAt;
}
