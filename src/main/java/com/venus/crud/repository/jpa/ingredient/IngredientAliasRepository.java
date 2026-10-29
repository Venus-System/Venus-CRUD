package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.IngredientAlias;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientAliasRepository extends JpaRepository<IngredientAlias, Long> {

    Optional<IngredientAlias> findByAliasNameIgnoreCase(String aliasName);
    List<IngredientAlias> findByIngredientId(Long ingredientId);
    Slice<IngredientAlias> findByIngredientId(Long ingredientId, Pageable pageable);

    @Query("""
            select ingredientAlias from IngredientAlias ingredientAlias
            where (cast(:aliasLanguage as String) is null or ingredientAlias.aliasLanguage = :aliasLanguage)
              and (cast(:sourceType as String) is null or ingredientAlias.sourceType = :sourceType)
            """)
    Slice<IngredientAlias> search(String aliasLanguage, SourceType sourceType, Pageable pageable);

    @Query("select a from IngredientAlias a join fetch a.ingredient where upper(a.aliasName) in :names")
    List<IngredientAlias> findWithIngredientByUpperAliasNameIn(@Param("names") Collection<String> names);
}
