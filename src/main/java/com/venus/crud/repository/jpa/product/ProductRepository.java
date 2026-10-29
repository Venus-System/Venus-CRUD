package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.product.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlug(String slug);

    @Query("""
            select product from Product product
            where (cast(:name as String) is null
                    or cast(unaccent(lower(product.name)) as String)
                          like cast(unaccent(lower(concat('%', cast(:name as String), '%'))) as String))
              and (:brandId is null or product.brand.id = :brandId)
              and (:productCategoryId is null or product.productCategory.id = :productCategoryId)
              and (:isActive is null or product.isActive = :isActive)
              and (cast(:ingredient as String) is null or exists (
                    select productIngredient.id from ProductIngredient productIngredient
                    where productIngredient.productVersion.product = product
                      and productIngredient.productVersion.isCurrent = true
                      and (cast(unaccent(lower(productIngredient.ingredient.inciName)) as String)
                              like cast(unaccent(lower(concat('%', cast(:ingredient as String), '%'))) as String)
                        or cast(unaccent(lower(productIngredient.ingredient.commonName)) as String)
                              like cast(unaccent(lower(concat('%', cast(:ingredient as String), '%'))) as String))))
              and (:hasCrueltyFreeClaim is null or product.brand.hasCrueltyFreeClaim = :hasCrueltyFreeClaim)
              and (:hasVeganClaim is null or product.brand.hasVeganClaim = :hasVeganClaim)
              and (:isBrazilian is null or product.brand.isBrazilian = :isBrazilian)
            """)
    Slice<Product> search(String name, Long brandId, Long productCategoryId, Boolean isActive, String ingredient,
            Boolean hasCrueltyFreeClaim, Boolean hasVeganClaim, Boolean isBrazilian, Pageable pageable);
}
