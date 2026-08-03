package com.venus.crud.entity.scan;

import com.venus.crud.entity.enums.RecommendationLevel;
import com.venus.crud.entity.enums.RiskLevel;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.entity.scoring.ScoringModel;
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
@Table(name = "personalized_scores")
@AttributeOverride(name = "id", column = @Column(name = "personalized_score_id"))
public class PersonalizedScore extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_version_id", nullable = false)
    private ProductVersion productVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_analysis_result_id", nullable = false)
    private AnalysisResult analysisResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_scoring_model_id", nullable = false)
    private ScoringModel scoringModel;

    @Column(name = "final_score", nullable = false)
    private Integer finalScore;

    @Column(name = "compatibility_percentage", nullable = false)
    private BigDecimal compatibilityPercentage;

    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "recommendation_level", nullable = false)
    private RecommendationLevel recommendationLevel;

    @Column(name = "summary", nullable = false)
    private String summary;
}
