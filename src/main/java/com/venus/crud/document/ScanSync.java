package com.venus.crud.document;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ScanSync {

    @Field("product_id")
    private Long productId;

    @Field("product_version_id")
    private Long productVersionId;

    @Field("synced_at")
    private OffsetDateTime syncedAt;

    private Integer attempts;

    @Field("last_error")
    private String lastError;

    @Field("failed_at")
    private OffsetDateTime failedAt;
}
