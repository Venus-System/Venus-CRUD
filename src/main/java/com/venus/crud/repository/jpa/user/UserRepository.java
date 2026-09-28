package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.enums.UserStatus;
import com.venus.crud.entity.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByFirebaseUid(String firebaseUid);
    Optional<User> findByEmailIgnoreCase(String email);
    Slice<User> findByStatus(UserStatus status, Pageable pageable);
    Slice<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Slice<User> findByStatusAndNameContainingIgnoreCase(UserStatus status, String name, Pageable pageable);
    Slice<User> findAllBy(Pageable pageable);

    @Query(value = "SELECT venus.fn_register_user_access(:userId, :accessType, CAST(:metadata AS jsonb))",
            nativeQuery = true)
    Long registerAccess(@Param("userId") Long userId, @Param("accessType") String accessType,
            @Param("metadata") String metadata);
}
