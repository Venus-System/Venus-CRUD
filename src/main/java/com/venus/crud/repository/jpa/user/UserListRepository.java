package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.enums.ListType;
import com.venus.crud.entity.user.UserList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserListRepository extends JpaRepository<UserList, Long> {

    List<UserList> findByUserId(Long userId);
    Slice<UserList> findByUserId(Long userId, Pageable pageable);
    Slice<UserList> findByUserIdAndListType(Long userId, ListType listType, Pageable pageable);
    Optional<UserList> findByUserIdAndName(Long userId, String name);
}
