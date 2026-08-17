package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.ingredient.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Long> {

    Optional<IngredientCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
