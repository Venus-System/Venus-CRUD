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
import com.venus.crud.repository.jpa.user.UserListRepository;
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
    private static final int POSITION_OF_UNIQUE_IMAGE = 0;

    private final MediaAssetRepository mediaAssetRepository;
    private final UserRepository userRepository;
    private final ProductVersionRepository productVersionRepository;
    private final UserListRepository userListRepository;
    private final MediaAssetMapper mediaAssetMapper;
    private final MediaProperties mediaProperties;

    public MediaAssetWriter(MediaAssetRepository mediaAssetRepository, UserRepository userRepository,
            ProductVersionRepository productVersionRepository, UserListRepository userListRepository,
            MediaAssetMapper mediaAssetMapper, MediaProperties mediaProperties) {
        this.mediaAssetRepository = mediaAssetRepository;
        this.userRepository = userRepository;
        this.productVersionRepository = productVersionRepository;
        this.userListRepository = userListRepository;
        this.mediaAssetMapper = mediaAssetMapper;
        this.mediaProperties = mediaProperties;
    }

    @Transactional(readOnly = true)
    public void validateUserExists(Long userId) {
        boolean exists = executeOrFail(() -> userRepository.existsById(userId), "Falha ao consultar usuario no banco de dados");
        if (!exists) {
            throw new ResourceNotFoundException("Usuario nao encontrado com id " + userId);
        }
    }

    @Transactional(readOnly = true)
    public void validateProductVersionExists(Long productVersionId) {
        boolean exists = executeOrFail(() -> productVersionRepository.existsById(productVersionId),
                "Falha ao consultar versao de produto no banco de dados");
        if (!exists) {
            throw new ResourceNotFoundException("Versao de produto nao encontrada com id " + productVersionId);
        }
    }

    @Transactional(readOnly = true)
    public void validateUserListExists(Long userListId) {
        boolean exists = executeOrFail(() -> userListRepository.existsById(userListId),
                "Falha ao consultar lista de usuario no banco de dados");
        if (!exists) {
            throw new ResourceNotFoundException("Lista de usuario nao encontrada com id " + userListId);
        }
    }

    @Transactional
    public MediaAsset registerAvatar(Long userId, CloudinaryUpload uploadedImage, String originalFilename) {
        findActiveAvatar(userId).ifPresent(this::markPreviousImageAsDeleted);

        MediaAsset newAvatar = createMedia(MediaPurpose.AVATAR, uploadedImage, originalFilename);
        newAvatar.setUser(userRepository.getReferenceById(userId));
        newAvatar.setSortOrder(POSITION_OF_UNIQUE_IMAGE);

        return executeOrFail(() -> mediaAssetRepository.save(newAvatar), "Falha ao registrar o avatar no banco de dados");
    }

    @Transactional
    public MediaAsset registerListCover(Long userListId, CloudinaryUpload uploadedImage, String originalFilename) {
        findActiveListCover(userListId).ifPresent(this::markPreviousImageAsDeleted);

        MediaAsset newCover = createMedia(MediaPurpose.LIST_COVER, uploadedImage, originalFilename);
        newCover.setUserList(userListRepository.getReferenceById(userListId));
        newCover.setSortOrder(POSITION_OF_UNIQUE_IMAGE);

        return executeOrFail(() -> mediaAssetRepository.save(newCover), "Falha ao registrar a capa da lista no banco de dados");
    }

    @Transactional
    public MediaAsset registerProductPhoto(Long productVersionId, CloudinaryUpload uploadedImage, String altText,
            Integer sortOrder, String originalFilename) {
        MediaAsset newPhoto = createMedia(MediaPurpose.PRODUCT_PHOTO, uploadedImage, originalFilename);
        newPhoto.setProductVersion(productVersionRepository.getReferenceById(productVersionId));
        newPhoto.setAltText(altText);
        newPhoto.setSortOrder(sortOrder != null ? sortOrder : nextSortOrder(productVersionId));

        return executeOrFail(() -> mediaAssetRepository.save(newPhoto), "Falha ao registrar a foto do produto no banco de dados");
    }

    @Transactional
    public MediaAsset patch(Long mediaAssetId, MediaAssetPatchRequest request) {
        MediaAsset asset = getOrThrow(mediaAssetId);
        mediaAssetMapper.patchEntity(request, asset);

        return executeOrFail(() -> mediaAssetRepository.save(asset), "Falha ao atualizar a midia no banco de dados");
    }

    @Transactional
    public MediaAsset markMediaAsDeleted(Long mediaAssetId) {
        return markImageAsDeleted(getOrThrow(mediaAssetId));
    }

    @Transactional
    public MediaAsset markAvatarAsDeleted(Long userId) {
        MediaAsset activeAvatar = findActiveAvatar(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Avatar nao encontrado para o usuario com id " + userId));

        return markImageAsDeleted(activeAvatar);
    }

    @Transactional
    public MediaAsset markListCoverAsDeleted(Long userListId) {
        MediaAsset activeCover = findActiveListCover(userListId)
                .orElseThrow(() -> new ResourceNotFoundException("Capa nao encontrada para a lista com id " + userListId));

        return markImageAsDeleted(activeCover);
    }

    @Transactional
    public Optional<MediaAsset> markListCoverAsDeletedIfExists(Long userListId) {
        return findActiveListCover(userListId).map(this::markImageAsDeleted);
    }

    private MediaAsset markImageAsDeleted(MediaAsset image) {
        image.setStatus(MediaStatus.DELETED);

        return executeOrFail(() -> mediaAssetRepository.save(image), "Falha ao remover a midia no banco de dados");
    }

    private void markPreviousImageAsDeleted(MediaAsset previousImage) {
        previousImage.setStatus(MediaStatus.DELETED);

        executeOrFail(() -> mediaAssetRepository.saveAndFlush(previousImage),
                "Falha ao substituir a imagem anterior no banco de dados");
    }

    private Optional<MediaAsset> findActiveAvatar(Long userId) {
        return executeOrFail(() -> mediaAssetRepository.findByUserIdAndPurposeAndStatusIn(
                        userId, MediaPurpose.AVATAR, MediaAssetRepository.LIVE_STATUSES),
                "Falha ao consultar o avatar atual do usuario");
    }

    private Optional<MediaAsset> findActiveListCover(Long userListId) {
        return executeOrFail(() -> mediaAssetRepository.findByUserListIdAndPurposeAndStatusIn(
                        userListId, MediaPurpose.LIST_COVER, MediaAssetRepository.LIVE_STATUSES),
                "Falha ao consultar a capa atual da lista");
    }

    private int nextSortOrder(Long productVersionId) {
        return executeOrFail(() -> mediaAssetRepository.findMaxSortOrder(productVersionId, MediaAssetRepository.LIVE_STATUSES),
                "Falha ao calcular a ordem da nova foto do produto") + 1;
    }

    private MediaAsset createMedia(MediaPurpose purpose, CloudinaryUpload uploadedImage, String originalFilename) {
        MediaAsset media = new MediaAsset();
        media.setPurpose(purpose);
        media.setProvider(PROVIDER);
        media.setResourceType(mediaProperties.defaultResourceType());
        media.setDeliveryType(mediaProperties.defaultDeliveryType());
        media.setPublicId(uploadedImage.publicId());
        media.setAssetId(uploadedImage.assetId());
        media.setVersion(uploadedImage.version());
        media.setSecureUrl(uploadedImage.secureUrl());
        media.setFormat(uploadedImage.format());
        media.setWidth(uploadedImage.width());
        media.setHeight(uploadedImage.height());
        media.setBytes(uploadedImage.bytes());
        media.setFolder(uploadedImage.folder());
        media.setOriginalFilename(originalFilename);
        media.setStatus(MediaStatus.ACTIVE);
        return media;
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
