package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.ingredient.ProductIngredient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Long> {

    @EntityGraph(attributePaths = "ingredient")
    List<ProductIngredient> findByProductVersionIdOrderByPosition(Long productVersionId);
    Slice<ProductIngredient> findByIngredientId(Long ingredientId, Pageable pageable);
    Optional<ProductIngredient> findByProductVersionIdAndIngredientId(Long productVersionId, Long ingredientId);
    boolean existsByProductVersionIdAndIngredientId(Long productVersionId, Long ingredientId);
    void deleteByProductVersionIdAndIngredientId(Long productVersionId, Long ingredientId);
    Slice<ProductIngredient> findAllBy(Pageable pageable);
}