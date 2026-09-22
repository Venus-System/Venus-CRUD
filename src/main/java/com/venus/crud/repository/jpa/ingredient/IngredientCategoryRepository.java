package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.ingredient.IngredientCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Long> {

    @EntityGraph(attributePaths = "parentCategory")
    Optional<IngredientCategory> findByNameIgnoreCase(String name);

    @EntityGraph(attributePaths = "parentCategory")
    Slice<IngredientCategory> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @EntityGraph(attributePaths = "parentCategory")
    Slice<IngredientCategory> findAllBy(Pageable pageable);

    List<IngredientCategory> findByParentCategoryId(Long parentCategoryId);

    @Override
    @EntityGraph(attributePaths = "parentCategory")
    Optional<IngredientCategory> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "parentCategory")
    List<IngredientCategory> findAll();
}
