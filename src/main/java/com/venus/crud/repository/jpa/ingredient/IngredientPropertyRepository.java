package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.IngredientProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientPropertyRepository extends JpaRepository<IngredientProperty, Long> {

    List<IngredientProperty> findByIngredientId(Long ingredientId);
    List<IngredientProperty> findByIngredientIdAndPropertyName(Long ingredientId, String propertyName);
    Slice<IngredientProperty> findBySourceType(SourceType sourceType, Pageable pageable);
    Slice<IngredientProperty> findAllBy(Pageable pageable);
}