package com.venus.crud.entity.scoring;

import com.venus.crud.entity.enums.RecommendationType;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.entity.scan.AnalysisResult;
import com.venus.crud.entity.shared.AuditableEntity;
import com.venus.crud.entity.shared.ProfileTag;
import com.venus.crud.entity.user.User;
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
@Table(name = "recommendations")
@AttributeOverride(name = "id", column = @Column(name = "recommendation_id"))
public class Recommendation extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_profile_tag_id", nullable = false)
    private ProfileTag profileTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_version_id", nullable = false)
    private ProductVersion productVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_analysis_result_id", nullable = false)
    private AnalysisResult analysisResult;

    @Column(name = "recommendation_type", nullable = false)
    private RecommendationType recommendationType;

    @Column(name = "confidence_score", nullable = false)
    private Short confidenceScore;

    @Column(name = "ranking_position", nullable = false)
    private Integer rankingPosition;

    @Column(name = "reason", nullable = false)
    private String reason;
}
