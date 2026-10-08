package com.venus.crud.repository.jpa.admin;

import com.venus.crud.entity.admin.AdminUser;
import com.venus.crud.entity.enums.AdminRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByEmail(String email);

    @Query("""
            select adminUser from AdminUser adminUser
            where (cast(:name as String) is null or lower(adminUser.name) like lower(concat('%', cast(:name as String), '%')))
              and (cast(:role as String) is null or adminUser.role = :role)
              and (:isActive is null or adminUser.isActive = :isActive)
            """)
    Slice<AdminUser> search(String name, AdminRole role, Boolean isActive, Pageable pageable);
}