package com.venus.crud.service.jpa.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.venus.crud.dto.jpa.response.media.MediaAssetResponse;
import com.venus.crud.entity.enums.MediaPurpose;
import com.venus.crud.entity.enums.MediaStatus;
import com.venus.crud.entity.media.MediaAsset;
import com.venus.crud.mapper.jpa.media.MediaAssetMapperImpl;
import com.venus.crud.repository.jpa.media.MediaAssetRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class MediaAssetServiceTest {

    private static final long USER_LIST_ID = 7L;

    @Mock
    private MediaAssetRepository mediaAssetRepository;

    @Mock
    private MediaAssetWriter mediaAssetWriter;

    @Mock
    private MediaFileValidator mediaFileValidator;

    @Mock
    private CloudinaryStorageService cloudinaryStorageService;

    private final MockMultipartFile coverFile = new MockMultipartFile("file", "capa.jpg", "image/jpeg", new byte[] {1, 2, 3});

    private MediaAssetService service;

    @BeforeEach
    void setUp() {
        service = new MediaAssetService(mediaAssetRepository, new MediaAssetMapperImpl(), mediaAssetWriter,
                mediaFileValidator, cloudinaryStorageService);
    }

    @Test
    void coverIsUploadedToTheListFolderAsListCover() {
        CloudinaryUpload uploadedImage = uploadedImage("user-lists/7/cover/nova");
        when(cloudinaryStorageService.upload(eq(coverFile), eq(MediaPurpose.LIST_COVER), startsWith("user-lists/7/cover/")))
                .thenReturn(uploadedImage);
        when(mediaAssetWriter.registerListCover(USER_LIST_ID, uploadedImage, "capa.jpg")).thenReturn(cover(uploadedImage));

        MediaAssetResponse response = service.uploadListCover(USER_LIST_ID, coverFile);

        verify(mediaFileValidator).validateFile(coverFile, MediaPurpose.LIST_COVER);
        verify(mediaAssetWriter).validateUserListExists(USER_LIST_ID);
        verify(mediaFileValidator).validateDimensions(uploadedImage, MediaPurpose.LIST_COVER);
        assertThat(response.purpose()).isEqualTo(MediaPurpose.LIST_COVER);
        assertThat(response.url()).isEqualTo(uploadedImage.secureUrl());
    }

    @Test
    void failedRegistrationRemovesTheImageThatWasJustUploaded() {
        CloudinaryUpload uploadedImage = uploadedImage("user-lists/7/cover/nova");
        when(cloudinaryStorageService.upload(eq(coverFile), eq(MediaPurpose.LIST_COVER), anyString())).thenReturn(uploadedImage);
        when(mediaAssetWriter.registerListCover(USER_LIST_ID, uploadedImage, "capa.jpg"))
                .thenThrow(new IllegalStateException("banco fora"));

        assertThatThrownBy(() -> service.uploadListCover(USER_LIST_ID, coverFile)).isInstanceOf(IllegalStateException.class);

        verify(cloudinaryStorageService).destroy("user-lists/7/cover/nova", MediaPurpose.LIST_COVER);
    }

    @Test
    void deletingTheCoverOfAListWithoutCoverDoesNotCallCloudinary() {
        when(mediaAssetWriter.markListCoverAsDeletedIfExists(USER_LIST_ID)).thenReturn(Optional.empty());

        service.deleteListCoverIfExists(USER_LIST_ID);

        verifyNoInteractions(cloudinaryStorageService);
    }

    @Test
    void deletingTheCoverOfAListWithCoverRemovesTheImageFromCloudinary() {
        MediaAsset deletedCover = cover(uploadedImage("user-lists/7/cover/antiga"));
        when(mediaAssetWriter.markListCoverAsDeletedIfExists(USER_LIST_ID)).thenReturn(Optional.of(deletedCover));

        service.deleteListCoverIfExists(USER_LIST_ID);

        verify(cloudinaryStorageService).destroy("user-lists/7/cover/antiga", MediaPurpose.LIST_COVER);
    }

    private CloudinaryUpload uploadedImage(String publicId) {
        return new CloudinaryUpload(publicId, "asset-" + publicId, 1L,
                "https://res.cloudinary.com/venus/image/upload/v1/" + publicId + ".jpg", "jpg", 800, 600, 1024L, "user-lists");
    }

    private MediaAsset cover(CloudinaryUpload uploadedImage) {
        MediaAsset cover = new MediaAsset();
        cover.setPurpose(MediaPurpose.LIST_COVER);
        cover.setPublicId(uploadedImage.publicId());
        cover.setSecureUrl(uploadedImage.secureUrl());
        cover.setStatus(MediaStatus.ACTIVE);
        return cover;
    }
}
