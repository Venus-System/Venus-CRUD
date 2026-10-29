package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.product.Brand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByNameIgnoreCase(String name);

    @Query("""
            select brand from Brand brand
            where (cast(:name as String) is null or lower(brand.name) like lower(concat('%', cast(:name as String), '%')))
              and (cast(:country as String) is null or brand.country = :country)
              and (:hasCrueltyFreeClaim is null or brand.hasCrueltyFreeClaim = :hasCrueltyFreeClaim)
              and (:hasVeganClaim is null or brand.hasVeganClaim = :hasVeganClaim)
              and (:isBrazilian is null or brand.isBrazilian = :isBrazilian)
            """)
    Slice<Brand> search(String name, String country, Boolean hasCrueltyFreeClaim, Boolean hasVeganClaim,
            Boolean isBrazilian, Pageable pageable);
}
