package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.IngredientAlias;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientAliasRepository extends JpaRepository<IngredientAlias, Long> {

    Optional<IngredientAlias> findByAliasNameIgnoreCase(String aliasName);
    List<IngredientAlias> findByIngredientId(Long ingredientId);
    Slice<IngredientAlias> findByIngredientId(Long ingredientId, Pageable pageable);
    Slice<IngredientAlias> findByAliasLanguage(String aliasLanguage, Pageable pageable);
    Slice<IngredientAlias> findBySourceType(SourceType sourceType, Pageable pageable);
    Slice<IngredientAlias> findAllBy(Pageable pageable);
}