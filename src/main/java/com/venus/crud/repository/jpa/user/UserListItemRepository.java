package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.user.UserListItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserListItemRepository extends JpaRepository<UserListItem, Long> {

    List<UserListItem> findByUserListIdOrderByPositionOrder(Long userListId);
    Slice<UserListItem> findByProductId(Long productId, Pageable pageable);
    Optional<UserListItem> findByUserListIdAndProductId(Long userListId, Long productId);
    boolean existsByUserListIdAndProductId(Long userListId, Long productId);
    void deleteByUserListIdAndProductId(Long userListId, Long productId);
}
