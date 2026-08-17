package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.enums.RoutineType;
import com.venus.crud.entity.user.Routine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {

    Page<Routine> findByUserId(Long userId, Pageable pageable);
    Page<Routine> findByUserIdAndRoutineType(Long userId, RoutineType routineType, Pageable pageable);
}
