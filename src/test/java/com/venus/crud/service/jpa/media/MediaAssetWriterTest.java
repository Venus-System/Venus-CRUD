package com.venus.crud.service.jpa.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.venus.crud.config.MediaProperties;
import com.venus.crud.entity.enums.MediaDeliveryType;
import com.venus.crud.entity.enums.MediaPurpose;
import com.venus.crud.entity.enums.MediaStatus;
import com.venus.crud.entity.media.MediaAsset;
import com.venus.crud.entity.user.UserList;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.media.MediaAssetMapperImpl;
import com.venus.crud.repository.jpa.media.MediaAssetRepository;
import com.venus.crud.repository.jpa.product.ProductVersionRepository;
import com.venus.crud.repository.jpa.user.UserListRepository;
import com.venus.crud.repository.jpa.user.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MediaAssetWriterTest {

    private static final long USER_LIST_ID = 7L;
    private static final CloudinaryUpload UPLOADED_IMAGE = new CloudinaryUpload("user-lists/7/cover/nova", "asset-nova", 1L,
            "https://res.cloudinary.com/venus/image/upload/v1/user-lists/7/cover/nova.jpg", "jpg", 800, 600, 1024L,
            "user-lists");

    @Mock
    private MediaAssetRepository mediaAssetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductVersionRepository productVersionRepository;

    @Mock
    private UserListRepository userListRepository;

    private MediaAssetWriter writer;

    @BeforeEach
    void setUp() {
        MediaProperties.Limits limits = new MediaProperties.Limits(2_000_000L, 2048, 2048);
        MediaProperties mediaProperties = new MediaProperties(List.of("image/jpeg"), MediaDeliveryType.UPLOAD, "image",
                limits, limits);
        writer = new MediaAssetWriter(mediaAssetRepository, userRepository, productVersionRepository, userListRepository,
                new MediaAssetMapperImpl(), mediaProperties);
    }

    @Test
    void newCoverReplacesThePreviousCover() {
        MediaAsset previousCover = new MediaAsset();
        previousCover.setPurpose(MediaPurpose.LIST_COVER);
        previousCover.setStatus(MediaStatus.ACTIVE);
        UserList userList = new UserList();
        userList.setId(USER_LIST_ID);
        when(mediaAssetRepository.findByUserListIdAndPurposeAndStatusIn(USER_LIST_ID, MediaPurpose.LIST_COVER,
                MediaAssetRepository.LIVE_STATUSES)).thenReturn(Optional.of(previousCover));
        when(userListRepository.getReferenceById(USER_LIST_ID)).thenReturn(userList);
        when(mediaAssetRepository.save(any(MediaAsset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MediaAsset registeredCover = writer.registerListCover(USER_LIST_ID, UPLOADED_IMAGE, "capa.jpg");

        assertThat(previousCover.getStatus()).isEqualTo(MediaStatus.DELETED);
        verify(mediaAssetRepository).saveAndFlush(previousCover);
        assertThat(registeredCover.getPurpose()).isEqualTo(MediaPurpose.LIST_COVER);
        assertThat(registeredCover.getUserList()).isSameAs(userList);
        assertThat(registeredCover.getUser()).isNull();
        assertThat(registeredCover.getProductVersion()).isNull();
        assertThat(registeredCover.getSortOrder()).isZero();
        assertThat(registeredCover.getStatus()).isEqualTo(MediaStatus.ACTIVE);
        assertThat(registeredCover.getPublicId()).isEqualTo(UPLOADED_IMAGE.publicId());
        assertThat(registeredCover.getSecureUrl()).isEqualTo(UPLOADED_IMAGE.secureUrl());
        assertThat(registeredCover.getOriginalFilename()).isEqualTo("capa.jpg");
    }

    @Test
    void listWithoutCoverHasNothingToMarkAsDeleted() {
        when(mediaAssetRepository.findByUserListIdAndPurposeAndStatusIn(USER_LIST_ID, MediaPurpose.LIST_COVER,
                MediaAssetRepository.LIVE_STATUSES)).thenReturn(Optional.empty());

        assertThat(writer.markListCoverAsDeletedIfExists(USER_LIST_ID)).isEmpty();
        verify(mediaAssetRepository, never()).save(any(MediaAsset.class));
    }

    @Test
    void markingACoverThatDoesNotExistReturns404() {
        when(mediaAssetRepository.findByUserListIdAndPurposeAndStatusIn(USER_LIST_ID, MediaPurpose.LIST_COVER,
                MediaAssetRepository.LIVE_STATUSES)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> writer.markListCoverAsDeleted(USER_LIST_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Capa nao encontrada para a lista com id 7");
    }

    @Test
    void listThatDoesNotExistReturns404BeforeTheUpload() {
        when(userListRepository.existsById(USER_LIST_ID)).thenReturn(false);

        assertThatThrownBy(() -> writer.validateUserListExists(USER_LIST_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Lista de usuario nao encontrada com id 7");
    }
}
