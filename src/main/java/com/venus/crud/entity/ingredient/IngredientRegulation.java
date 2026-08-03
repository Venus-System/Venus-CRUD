package com.venus.crud.entity.ingredient;

import com.venus.crud.entity.enums.RestrictionType;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.shared.AuditableEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "ingredient_regulations")
@AttributeOverride(name = "id", column = @Column(name = "ingredient_regulation_id"))
public class IngredientRegulation extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_ingredient_id", nullable = false)
    private Ingredient ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_regulation_id", nullable = false)
    private Regulation regulation;

    @Column(name = "restriction_type", nullable = false)
    private RestrictionType restrictionType;

    @Column(name = "max_concentration_value", precision = 10, scale = 4)
    private BigDecimal maxConcentrationValue;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "notes", nullable = false)
    private String notes;

    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Column(name = "source_reference")
    private String sourceReference;
}
