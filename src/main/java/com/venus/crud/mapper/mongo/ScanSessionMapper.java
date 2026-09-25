package com.venus.crud.mapper.mongo;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.document.IngredientCandidate;
import com.venus.crud.document.IngredientMatch;
import com.venus.crud.document.ScanBackExtracted;
import com.venus.crud.document.ScanDevice;
import com.venus.crud.document.ScanFrontExtracted;
import com.venus.crud.document.ScanImage;
import com.venus.crud.document.ScanImages;
import com.venus.crud.document.ScanIngredient;
import com.venus.crud.document.ScanOcr;
import com.venus.crud.document.ScanOcrBack;
import com.venus.crud.document.ScanOcrFront;
import com.venus.crud.document.ScanQualityCheck;
import com.venus.crud.document.ScanSession;
import com.venus.crud.document.ScanSource;
import com.venus.crud.dto.mongo.request.ScanBackExtractedRequest;
import com.venus.crud.dto.mongo.request.ScanDeviceRequest;
import com.venus.crud.dto.mongo.request.ScanFrontExtractedRequest;
import com.venus.crud.dto.mongo.request.ScanImageRequest;
import com.venus.crud.dto.mongo.request.ScanImagesRequest;
import com.venus.crud.dto.mongo.request.ScanIngredientRequest;
import com.venus.crud.dto.mongo.request.ScanOcrBackRequest;
import com.venus.crud.dto.mongo.request.ScanOcrFrontRequest;
import com.venus.crud.dto.mongo.request.ScanOcrRequest;
import com.venus.crud.dto.mongo.request.ScanQualityCheckRequest;
import com.venus.crud.dto.mongo.request.ScanSessionRequest;
import com.venus.crud.dto.mongo.response.IngredientCandidateResponse;
import com.venus.crud.dto.mongo.response.IngredientMatchResponse;
import com.venus.crud.dto.mongo.response.ScanBackExtractedResponse;
import com.venus.crud.dto.mongo.response.ScanDeviceResponse;
import com.venus.crud.dto.mongo.response.ScanFrontExtractedResponse;
import com.venus.crud.dto.mongo.response.ScanImageResponse;
import com.venus.crud.dto.mongo.response.ScanImagesResponse;
import com.venus.crud.dto.mongo.response.ScanIngredientResponse;
import com.venus.crud.dto.mongo.response.ScanOcrBackResponse;
import com.venus.crud.dto.mongo.response.ScanOcrFrontResponse;
import com.venus.crud.dto.mongo.response.ScanOcrResponse;
import com.venus.crud.dto.mongo.response.ScanQualityCheckResponse;
import com.venus.crud.dto.mongo.response.ScanSessionResponse;
import com.venus.crud.dto.mongo.response.ScanSourceResponse;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = VenusMapperConfig.class)
public interface ScanSessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "source", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ScanSession toEntity(ScanSessionRequest request);

    ScanDevice toEntity(ScanDeviceRequest request);

    ScanQualityCheck toEntity(ScanQualityCheckRequest request);

    ScanImages toEntity(ScanImagesRequest request);

    @Mapping(target = "secureUrl", ignore = true)
    ScanImage toEntity(ScanImageRequest request);

    ScanOcr toEntity(ScanOcrRequest request);

    ScanOcrFront toEntity(ScanOcrFrontRequest request);

    ScanOcrBack toEntity(ScanOcrBackRequest request);

    ScanFrontExtracted toEntity(ScanFrontExtractedRequest request);

    ScanBackExtracted toEntity(ScanBackExtractedRequest request);

    @Mapping(target = "normalizedName", ignore = true)
    @Mapping(target = "match", ignore = true)
    ScanIngredient toEntity(ScanIngredientRequest request);

    ScanSessionResponse toResponse(ScanSession document);

    ScanSourceResponse toResponse(ScanSource source);

    ScanDeviceResponse toResponse(ScanDevice device);

    ScanQualityCheckResponse toResponse(ScanQualityCheck qualityCheck);

    ScanImagesResponse toResponse(ScanImages images);

    ScanImageResponse toResponse(ScanImage image);

    ScanOcrResponse toResponse(ScanOcr ocr);

    ScanOcrFrontResponse toResponse(ScanOcrFront ocrFront);

    ScanOcrBackResponse toResponse(ScanOcrBack ocrBack);

    ScanFrontExtractedResponse toResponse(ScanFrontExtracted extracted);

    ScanBackExtractedResponse toResponse(ScanBackExtracted extracted);

    ScanIngredientResponse toResponse(ScanIngredient ingredient);

    IngredientMatchResponse toResponse(IngredientMatch match);

    IngredientCandidateResponse toResponse(IngredientCandidate candidate);

    default String map(UUID value) {
        return value == null ? null : value.toString();
    }
}
