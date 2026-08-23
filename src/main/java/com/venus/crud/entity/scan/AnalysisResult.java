package com.venus.crud.entity.scan;

import com.venus.crud.entity.enums.AnalysisStatus;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "analysis_results")
@AttributeOverride(name = "id", column = @Column(name = "analysis_result_id"))
public class AnalysisResult extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_version_id", nullable = false)
    private ProductVersion productVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_scoring_model_id", nullable = false)
    private ScoringModel scoringModel;

    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;

    @Column(name = "health_score", nullable = false)
    private Integer healthScore;

    @Column(name = "environmental_score", nullable = false)
    private Integer environmentalScore;

    @Column(name = "ethical_score", nullable = false)
    private Integer ethicalScore;

    @Column(name = "performance_score", nullable = false)
    private Integer performanceScore;

    @Column(name = "transparency_score", nullable = false)
    private Integer transparencyScore;

    @Column(name = "confidence_score", nullable = false)
    private Short confidenceScore;

    @Column(name = "processing_time_ms", nullable = false)
    private Integer processingTimeMs;

    @Column(name = "status", nullable = false)
    private AnalysisStatus status;

    @Column(name = "summary", nullable = false)
    private String summary;
}
