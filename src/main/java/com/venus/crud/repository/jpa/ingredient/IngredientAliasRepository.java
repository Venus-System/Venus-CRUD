package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.IngredientAlias;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientAliasRepository extends JpaRepository<IngredientAlias, Long> {

    @EntityGraph(attributePaths = "ingredient")
    Optional<IngredientAlias> findByAliasNameIgnoreCase(String aliasName);
    List<IngredientAlias> findByIngredientId(Long ingredientId);
    Page<IngredientAlias> findByAliasLanguage(String aliasLanguage, Pageable pageable);
    Page<IngredientAlias> findBySourceType(SourceType sourceType, Pageable pageable);
}
