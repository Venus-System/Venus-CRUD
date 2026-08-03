package com.venus.crud.entity.shared;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@MappedSuperclass
public abstract class AuditableEntity extends BaseEntity {

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
