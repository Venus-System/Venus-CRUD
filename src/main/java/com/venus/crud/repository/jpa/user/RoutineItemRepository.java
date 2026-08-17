package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.enums.RoutineTime;
import com.venus.crud.entity.user.RoutineItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineItemRepository extends JpaRepository<RoutineItem, Long> {

    @EntityGraph(attributePaths = "product")
    List<RoutineItem> findByRoutineIdOrderByStepOrder(Long routineId);
    Page<RoutineItem> findByProductId(Long productId, Pageable pageable);
    List<RoutineItem> findByRoutineIdAndUsageTime(Long routineId, RoutineTime usageTime);
    boolean existsByRoutineIdAndProductId(Long routineId, Long productId);
    void deleteByRoutineIdAndProductId(Long routineId, Long productId);
}
