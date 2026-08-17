package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.enums.ListType;
import com.venus.crud.entity.user.UserList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserListRepository extends JpaRepository<UserList, Long> {

    Page<UserList> findByUserId(Long userId, Pageable pageable);
    Page<UserList> findByUserIdAndListType(Long userId, ListType listType, Pageable pageable);
    boolean existsByUserIdAndName(Long userId, String name);
}
