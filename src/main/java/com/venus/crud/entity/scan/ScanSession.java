package com.venus.crud.entity.scan;

import com.venus.crud.entity.enums.ScanStatus;
import com.venus.crud.entity.enums.ScanStep;
import com.venus.crud.entity.enums.ScanType;
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
@Table(name = "scan_sessions")
@AttributeOverride(name = "id", column = @Column(name = "scan_session_id"))
public class ScanSession extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", nullable = false)
    private User user;

    @Column(name = "scan_type", nullable = false)
    private ScanType scanType;

    @Column(name = "status", nullable = false)
    private ScanStatus status;

    @Column(name = "current_step", nullable = false)
    private ScanStep currentStep;

    @Column(name = "device_info")
    private String deviceInfo;
}
