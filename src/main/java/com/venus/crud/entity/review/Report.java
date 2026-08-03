package com.venus.crud.entity.review;

import com.venus.crud.entity.admin.AdminUser;
import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
import com.venus.crud.entity.shared.AuditableEntity;
import com.venus.crud.entity.user.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "reports")
@AttributeOverride(name = "id", column = @Column(name = "report_id"))
public class Report extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_admin_user_id")
    private AdminUser adminUser;

    @Column(name = "target_type", nullable = false)
    private ReportTargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Column(name = "handled_at")
    private OffsetDateTime handledAt;
}
