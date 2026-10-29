package com.venus.crud.entity.ingredient;

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
@Table(name = "ingredient_aliases")
@AttributeOverride(name = "id", column = @Column(name = "ingredient_alias_id"))
public class IngredientAlias extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "alias_name", nullable = false)
    private String aliasName;

    @Column(name = "alias_language", nullable = false)
    private String aliasLanguage;

    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Column(name = "source_reference")
    private String sourceReference;
}
