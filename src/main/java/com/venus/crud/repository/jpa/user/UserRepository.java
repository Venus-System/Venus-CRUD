package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.enums.UserStatus;
import com.venus.crud.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByFirebaseUid(String firebaseUid);
    boolean existsByFirebaseUid(String firebaseUid);
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
