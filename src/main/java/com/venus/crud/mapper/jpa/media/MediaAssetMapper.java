package com.venus.crud.mapper.jpa.media;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.media.MediaAssetPatchRequest;
import com.venus.crud.dto.jpa.response.media.MediaAssetResponse;
import com.venus.crud.entity.media.MediaAsset;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface MediaAssetMapper {

    @Mapping(target = "mediaAssetId", source = "id")
    @Mapping(target = "url", source = "secureUrl")
    MediaAssetResponse toResponse(MediaAsset entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "productVersion", ignore = true)
    @Mapping(target = "purpose", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "resourceType", ignore = true)
    @Mapping(target = "deliveryType", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "assetId", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "secureUrl", ignore = true)
    @Mapping(target = "format", ignore = true)
    @Mapping(target = "width", ignore = true)
    @Mapping(target = "height", ignore = true)
    @Mapping(target = "bytes", ignore = true)
    @Mapping(target = "folder", ignore = true)
    @Mapping(target = "originalFilename", ignore = true)
    @Mapping(target = "status", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(MediaAssetPatchRequest request, @MappingTarget MediaAsset entity);
}
