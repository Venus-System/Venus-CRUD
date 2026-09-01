package com.venus.crud.repository.jpa.admin;

import com.venus.crud.entity.admin.AdminUser;
import com.venus.crud.entity.enums.AdminRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByEmail(String email);
    Slice<AdminUser> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Slice<AdminUser> findByRole(AdminRole role, Pageable pageable);
    Slice<AdminUser> findByIsActiveTrue(Pageable pageable);
    Slice<AdminUser> findAllBy(Pageable pageable);
}