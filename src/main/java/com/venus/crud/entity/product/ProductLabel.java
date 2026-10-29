package com.venus.crud.entity.product;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.shared.AuditableEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "product_labels")
@AttributeOverride(name = "id", column = @Column(name = "product_label_id"))
public class ProductLabel extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_version_id", nullable = false, unique = true)
    private ProductVersion productVersion;

    @Column(name = "normalized_text", nullable = false)
    private String normalizedText;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Column(name = "source_reference")
    private String sourceReference;
}
