package com.venus.crud.entity.admin;
import com.venus.crud.entity.shared.AuditableEntity;

import com.venus.crud.entity.enums.AdminRole;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "admin_users")
@AttributeOverride(name = "id", column = @Column(name = "admin_user_id"))
public class AdminUser extends AuditableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "role", nullable = false)
    private AdminRole role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
