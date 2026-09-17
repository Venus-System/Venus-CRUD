package com.venus.crud.mapper.mongo;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.document.ScanDevice;
import com.venus.crud.document.ScanQualityCheck;
import com.venus.crud.document.ScanSession;
import com.venus.crud.dto.mongo.request.ScanDeviceRequest;
import com.venus.crud.dto.mongo.request.ScanQualityCheckRequest;
import com.venus.crud.dto.mongo.request.ScanSessionRequest;
import com.venus.crud.dto.mongo.response.ScanDeviceResponse;
import com.venus.crud.dto.mongo.response.ScanQualityCheckResponse;
import com.venus.crud.dto.mongo.response.ScanSessionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = VenusMapperConfig.class)
public interface ScanSessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ScanSession toEntity(ScanSessionRequest request);

    ScanDevice toEntity(ScanDeviceRequest request);

    ScanQualityCheck toEntity(ScanQualityCheckRequest request);

    ScanSessionResponse toResponse(ScanSession document);

    ScanDeviceResponse toResponse(ScanDevice device);

    ScanQualityCheckResponse toResponse(ScanQualityCheck qualityCheck);
}
