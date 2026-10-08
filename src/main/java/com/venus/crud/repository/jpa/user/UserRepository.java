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

    @Query("""
            select user from User user
            where (cast(:status as String) is null or user.status = :status)
              and (cast(:name as String) is null or lower(user.name) like lower(concat('%', cast(:name as String), '%')))
              and (cast(:firebaseUid as String) is null or user.firebaseUid = :firebaseUid)
            """)
    Slice<User> search(UserStatus status, String name, String firebaseUid, Pageable pageable);

    @Query(value = "SELECT venus.fn_register_user_access(:userId, :accessType, CAST(:metadata AS jsonb))",
            nativeQuery = true)
    Long registerAccess(@Param("userId") Long userId, @Param("accessType") String accessType,
            @Param("metadata") String metadata);
}
