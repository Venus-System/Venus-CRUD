package com.venus.crud.service.jpa.media;

import com.venus.crud.dto.jpa.patch.media.MediaAssetPatchRequest;
import com.venus.crud.dto.jpa.response.media.MediaAssetResponse;
import com.venus.crud.entity.enums.MediaPurpose;
import com.venus.crud.entity.media.MediaAsset;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.exception.DataIntegrityViolationTranslator;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.media.MediaAssetMapper;
import com.venus.crud.repository.jpa.media.MediaAssetRepository;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaAssetService {

    private static final Logger log = LoggerFactory.getLogger(MediaAssetService.class);

    private final MediaAssetRepository mediaAssetRepository;
    private final MediaAssetMapper mediaAssetMapper;
    private final MediaAssetWriter mediaAssetWriter;
    private final MediaFileValidator mediaFileValidator;
    private final CloudinaryStorageService cloudinaryStorageService;

    public MediaAssetService(MediaAssetRepository mediaAssetRepository, MediaAssetMapper mediaAssetMapper,
            MediaAssetWriter mediaAssetWriter, MediaFileValidator mediaFileValidator,
            CloudinaryStorageService cloudinaryStorageService) {
        this.mediaAssetRepository = mediaAssetRepository;
        this.mediaAssetMapper = mediaAssetMapper;
        this.mediaAssetWriter = mediaAssetWriter;
        this.mediaFileValidator = mediaFileValidator;
        this.cloudinaryStorageService = cloudinaryStorageService;
    }

    @Transactional(readOnly = true)
    public MediaAssetResponse findAvatar(Long userId) {
        return executeOrFail(() -> mediaAssetRepository.findByUserIdAndPurposeAndStatusIn(
                        userId, MediaPurpose.AVATAR, MediaAssetRepository.LIVE_STATUSES),
                "Falha ao consultar o avatar do usuario")
                .map(mediaAssetMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Avatar nao encontrado para o usuario com id " + userId));
    }

    @Transactional(readOnly = true)
    public List<MediaAssetResponse> findProductPhotos(Long productVersionId) {
        return executeOrFail(() -> mediaAssetRepository.findByProductVersionIdAndPurposeAndStatusInOrderBySortOrderAscIdAsc(
                        productVersionId, MediaPurpose.PRODUCT_PHOTO, MediaAssetRepository.LIVE_STATUSES),
                "Falha ao consultar as fotos da versao de produto").stream()
                .map(mediaAssetMapper::toResponse)
                .toList();
    }

    public MediaAssetResponse uploadAvatar(Long userId, MultipartFile file) {
        mediaFileValidator.validateFile(file, MediaPurpose.AVATAR);
        mediaAssetWriter.ensureUserExists(userId);

        String publicId = "users/" + userId + "/avatar/" + UUID.randomUUID();
        CloudinaryUpload upload = cloudinaryStorageService.upload(file, MediaPurpose.AVATAR, publicId);

        MediaAsset saved = registerOrCompensate(upload, MediaPurpose.AVATAR,
                () -> mediaAssetWriter.registerAvatar(userId, upload, file.getOriginalFilename()));
        return mediaAssetMapper.toResponse(saved);
    }

    public MediaAssetResponse uploadProductPhoto(Long productVersionId, MultipartFile file, String altText, Integer sortOrder) {
        mediaFileValidator.validateFile(file, MediaPurpose.PRODUCT_PHOTO);
        mediaAssetWriter.ensureProductVersionExists(productVersionId);

        String publicId = "product-versions/" + productVersionId + "/photos/" + UUID.randomUUID();
        CloudinaryUpload upload = cloudinaryStorageService.upload(file, MediaPurpose.PRODUCT_PHOTO, publicId);

        MediaAsset saved = registerOrCompensate(upload, MediaPurpose.PRODUCT_PHOTO,
                () -> mediaAssetWriter.registerProductPhoto(productVersionId, upload, altText, sortOrder, file.getOriginalFilename()));
        return mediaAssetMapper.toResponse(saved);
    }

    public MediaAssetResponse patch(Long mediaAssetId, MediaAssetPatchRequest request) {
        return mediaAssetMapper.toResponse(mediaAssetWriter.patch(mediaAssetId, request));
    }

    public void delete(Long mediaAssetId) {
        removeFromStorage(mediaAssetWriter.markDeleted(mediaAssetId));
    }

    public void deleteAvatar(Long userId) {
        removeFromStorage(mediaAssetWriter.markAvatarDeleted(userId));
    }

    private void removeFromStorage(MediaAsset deleted) {
        destroyQuietly(deleted.getPublicId(), deleted.getPurpose());
    }

    private MediaAsset registerOrCompensate(CloudinaryUpload upload, MediaPurpose purpose, Supplier<MediaAsset> registration) {
        try {
            mediaFileValidator.validateDimensions(upload, purpose);
            return registration.get();
        } catch (RuntimeException ex) {
            destroyQuietly(upload.publicId(), purpose);
            throw ex;
        }
    }

    private void destroyQuietly(String publicId, MediaPurpose purpose) {
        try {
            cloudinaryStorageService.destroy(publicId, purpose);
        } catch (RuntimeException ex) {
            log.error("Falha ao remover o asset {} do Cloudinary; o arquivo pode ter ficado orfao e precisa de reconciliacao", publicId, ex);
        }
    }

    private <T> T executeOrFail(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException ex) {
            throw DataIntegrityViolationTranslator.translate(ex);
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw DataAccessFailureTranslator.translate(ex, errorMessage);
        }
    }
}
