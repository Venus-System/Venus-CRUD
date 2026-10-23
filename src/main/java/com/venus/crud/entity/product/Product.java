package com.venus.crud.entity.product;

import com.venus.crud.entity.shared.AuditableEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Formula;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "products")
@AttributeOverride(name = "id", column = @Column(name = "product_id"))
public class Product extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_brand_id", nullable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_category_id", nullable = false)
    private ProductCategory productCategory;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Setter(AccessLevel.NONE)
    @Formula("(SELECT m.secure_url FROM venus.media_assets m WHERE m.fk_product_version_id = "
            + "(SELECT v.product_version_id FROM venus.product_versions v WHERE v.fk_product_id = product_id AND v.is_current) "
            + "AND m.purpose = 'product_photo' AND m.status IN ('pending', 'active') "
            + "ORDER BY m.sort_order, m.media_asset_id LIMIT 1)")
    private String imageUrl;
}
