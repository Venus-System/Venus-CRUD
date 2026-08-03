package com.venus.crud.entity.review;

import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.entity.shared.AuditableEntity;
import com.venus.crud.entity.user.User;
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
@Table(name = "reviews")
@AttributeOverride(name = "id", column = @Column(name = "review_id"))
public class Review extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_version_id", nullable = false)
    private ProductVersion productVersion;

    @Column(name = "rating", nullable = false)
    private BigDecimal rating;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "verified_use", nullable = false)
    private Boolean verifiedUse;
}
