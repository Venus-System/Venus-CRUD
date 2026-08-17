package com.venus.crud.mapper.product;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.product.ClaimPatchRequest;
import com.venus.crud.dto.request.product.ClaimRequest;
import com.venus.crud.dto.response.product.ClaimResponse;
import com.venus.crud.entity.product.Claim;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface ClaimMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Claim toEntity(ClaimRequest request);

    ClaimResponse toResponse(Claim entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ClaimRequest request, @MappingTarget Claim entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntity(ClaimPatchRequest request, @MappingTarget Claim entity);
}
