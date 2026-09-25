package com.venus.crud.service.jpa.media;

import com.venus.crud.config.MediaProperties;
import com.venus.crud.dto.jpa.patch.media.MediaAssetPatchRequest;
import com.venus.crud.entity.enums.MediaPurpose;
import com.venus.crud.entity.enums.MediaStatus;
import com.venus.crud.entity.media.MediaAsset;
import com.venus.crud.exception.DataAccessFailureTranslator;
import com.venus.crud.exception.DataIntegrityViolationTranslator;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.media.MediaAssetMapper;
import com.venus.crud.repository.jpa.media.MediaAssetRepository;
import com.venus.crud.repository.jpa.product.ProductVersionRepository;
import com.venus.crud.repository.jpa.user.UserRepository;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MediaAssetWriter {

    private static final Logger log = LoggerFactory.getLogger(MediaAssetWriter.class);

    private static final String PROVIDER = "cloudinary";
    private static final int AVATAR_SORT_ORDER = 0;

    private final MediaAssetRepository mediaAssetRepository;
    private final UserRepository userRepository;
    private final ProductVersionRepository productVersionRepository;
    private final MediaAssetMapper mediaAssetMapper;
    private final MediaProperties mediaProperties;

    public MediaAssetWriter(MediaAssetRepository mediaAssetRepository, UserRepository userRepository,
            ProductVersionRepository productVersionRepository, MediaAssetMapper mediaAssetMapper,
            MediaProperties mediaProperties) {
        this.mediaAssetRepository = mediaAssetRepository;
        this.userRepository = userRepository;
        this.productVersionRepository = productVersionRepository;
        this.mediaAssetMapper = mediaAssetMapper;
        this.mediaProperties = mediaProperties;
    }

    @Transactional(readOnly = true)
    public void ensureUserExists(Long userId) {
        boolean exists = executeOrFail(() -> userRepository.existsById(userId), "Falha ao consultar usuario no banco de dados");
        if (!exists) {
            throw new ResourceNotFoundException("Usuario nao encontrado com id " + userId);
        }
    }

    @Transactional(readOnly = true)
    public void ensureProductVersionExists(Long productVersionId) {
        boolean exists = executeOrFail(() -> productVersionRepository.existsById(productVersionId),
                "Falha ao consultar versao de produto no banco de dados");
        if (!exists) {
            throw new ResourceNotFoundException("Versao de produto nao encontrada com id " + productVersionId);
        }
    }

    @Transactional
    public MediaAsset registerAvatar(Long userId, CloudinaryUpload upload, String originalFilename) {
        deactivateCurrentAvatar(userId);

        MediaAsset asset = newAsset(MediaPurpose.AVATAR, upload, originalFilename);
        asset.setUser(userRepository.getReferenceById(userId));
        asset.setSortOrder(AVATAR_SORT_ORDER);

        return executeOrFail(() -> mediaAssetRepository.save(asset), "Falha ao registrar o avatar no banco de dados");
    }

    @Transactional
    public MediaAsset registerProductPhoto(Long productVersionId, CloudinaryUpload upload, String altText,
            Integer sortOrder, String originalFilename) {
        MediaAsset asset = newAsset(MediaPurpose.PRODUCT_PHOTO, upload, originalFilename);
        asset.setProductVersion(productVersionRepository.getReferenceById(productVersionId));
        asset.setAltText(altText);
        asset.setSortOrder(sortOrder != null ? sortOrder : nextSortOrder(productVersionId));

        return executeOrFail(() -> mediaAssetRepository.save(asset), "Falha ao registrar a foto do produto no banco de dados");
    }

    @Transactional
    public MediaAsset patch(Long mediaAssetId, MediaAssetPatchRequest request) {
        MediaAsset asset = getOrThrow(mediaAssetId);
        mediaAssetMapper.patchEntity(request, asset);

        return executeOrFail(() -> mediaAssetRepository.save(asset), "Falha ao atualizar a midia no banco de dados");
    }

    @Transactional
    public MediaAsset markDeleted(Long mediaAssetId) {
        return deleteAsset(getOrThrow(mediaAssetId));
    }

    @Transactional
    public MediaAsset markAvatarDeleted(Long userId) {
        MediaAsset asset = findCurrentAvatar(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Avatar nao encontrado para o usuario com id " + userId));

        return deleteAsset(asset);
    }

    private MediaAsset deleteAsset(MediaAsset asset) {
        asset.setStatus(MediaStatus.DELETED);

        return executeOrFail(() -> mediaAssetRepository.save(asset), "Falha ao remover a midia no banco de dados");
    }

    private Optional<MediaAsset> findCurrentAvatar(Long userId) {
        return executeOrFail(() -> mediaAssetRepository.findByUserIdAndPurposeAndStatusIn(
                        userId, MediaPurpose.AVATAR, MediaAssetRepository.LIVE_STATUSES),
                "Falha ao consultar o avatar atual do usuario");
    }

    private void deactivateCurrentAvatar(Long userId) {
        findCurrentAvatar(userId).ifPresent(current -> {
            current.setStatus(MediaStatus.DELETED);
            executeOrFail(() -> mediaAssetRepository.saveAndFlush(current), "Falha ao substituir o avatar atual do usuario");
        });
    }

    private int nextSortOrder(Long productVersionId) {
        return executeOrFail(() -> mediaAssetRepository.findMaxSortOrder(productVersionId, MediaAssetRepository.LIVE_STATUSES),
                "Falha ao calcular a ordem da nova foto do produto") + 1;
    }

    private MediaAsset newAsset(MediaPurpose purpose, CloudinaryUpload upload, String originalFilename) {
        MediaAsset asset = new MediaAsset();
        asset.setPurpose(purpose);
        asset.setProvider(PROVIDER);
        asset.setResourceType(mediaProperties.defaultResourceType());
        asset.setDeliveryType(mediaProperties.defaultDeliveryType());
        asset.setPublicId(upload.publicId());
        asset.setAssetId(upload.assetId());
        asset.setVersion(upload.version());
        asset.setSecureUrl(upload.secureUrl());
        asset.setFormat(upload.format());
        asset.setWidth(upload.width());
        asset.setHeight(upload.height());
        asset.setBytes(upload.bytes());
        asset.setFolder(upload.folder());
        asset.setOriginalFilename(originalFilename);
        asset.setStatus(MediaStatus.ACTIVE);
        return asset;
    }

    private MediaAsset getOrThrow(Long mediaAssetId) {
        MediaAsset asset = executeOrFail(() -> mediaAssetRepository.findById(mediaAssetId), "Falha ao consultar midia no banco de dados")
                .orElseThrow(() -> new ResourceNotFoundException("Midia nao encontrada com id " + mediaAssetId));

        if (!MediaAssetRepository.LIVE_STATUSES.contains(asset.getStatus())) {
            throw new ResourceNotFoundException("Midia nao encontrada com id " + mediaAssetId);
        }

        return asset;
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
