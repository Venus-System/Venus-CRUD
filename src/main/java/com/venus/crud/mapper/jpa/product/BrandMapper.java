package com.venus.crud.mapper.jpa.product;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.product.BrandPatchRequest;
import com.venus.crud.dto.jpa.request.product.BrandRequest;
import com.venus.crud.dto.jpa.response.product.BrandResponse;
import com.venus.crud.entity.product.Brand;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface BrandMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Brand toEntity(BrandRequest request);

    BrandResponse toResponse(Brand entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(BrandRequest request, @MappingTarget Brand entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntity(BrandPatchRequest request, @MappingTarget Brand entity);
}
