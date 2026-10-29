package com.venus.crud.entity.ingredient;

import com.venus.crud.entity.enums.RegulationStatus;
import com.venus.crud.entity.shared.AuditableEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "regulations")
@AttributeOverride(name = "id", column = @Column(name = "regulation_id"))
public class Regulation extends AuditableEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "agency", nullable = false)
    private String agency;

    @Column(name = "document_url", nullable = false)
    private String documentUrl;

    @Column(name = "status", nullable = false)
    private RegulationStatus status;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "summary", nullable = false)
    private String summary;
}
