package com.venus.crud.repository.jpa.admin;

import com.venus.crud.entity.admin.AdminUser;
import com.venus.crud.entity.enums.AdminRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<AdminUser> findByRole(AdminRole role, Pageable pageable);
    Page<AdminUser> findByIsActiveTrue(Pageable pageable);
}
