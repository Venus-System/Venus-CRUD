package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.user.UserListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserListItemRepository extends JpaRepository<UserListItem, Long> {

    @EntityGraph(attributePaths = "product")
    List<UserListItem> findByUserListIdOrderByPositionOrder(Long userListId);
    Page<UserListItem> findByProductId(Long productId, Pageable pageable);
    boolean existsByUserListIdAndProductId(Long userListId, Long productId);
    void deleteByUserListIdAndProductId(Long userListId, Long productId);
}
