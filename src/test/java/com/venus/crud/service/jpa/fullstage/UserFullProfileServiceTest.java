package com.venus.crud.service.jpa.fullstage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.venus.crud.dto.jpa.response.fullstage.UserAllergyDetailResponse;
import com.venus.crud.dto.jpa.response.fullstage.UserFullProfileResponse;
import com.venus.crud.entity.enums.MediaPurpose;
import com.venus.crud.entity.enums.RiskLevel;
import com.venus.crud.entity.user.User;
import com.venus.crud.entity.user.UserAllergy;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.media.MediaAssetMapper;
import com.venus.crud.mapper.jpa.product.ProductMapper;
import com.venus.crud.mapper.jpa.shared.ProfileTagMapper;
import com.venus.crud.mapper.jpa.user.AllergyMapper;
import com.venus.crud.mapper.jpa.user.UserListMapper;
import com.venus.crud.mapper.jpa.user.UserMapper;
import com.venus.crud.mapper.jpa.user.UserPreferenceMapper;
import com.venus.crud.mapper.jpa.user.UserProfileMapper;
import com.venus.crud.repository.jpa.media.MediaAssetRepository;
import com.venus.crud.repository.jpa.user.FavoriteRepository;
import com.venus.crud.repository.jpa.user.UserAllergyRepository;
import com.venus.crud.repository.jpa.user.UserListItemRepository;
import com.venus.crud.repository.jpa.user.UserListRepository;
import com.venus.crud.repository.jpa.user.UserPreferenceRepository;
import com.venus.crud.repository.jpa.user.UserProfileRepository;
import com.venus.crud.repository.jpa.user.UserProfileTagRepository;
import com.venus.crud.repository.jpa.user.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserFullProfileServiceTest {

    private static final Long USER_ID = 7L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MediaAssetRepository mediaAssetRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserProfileTagRepository userProfileTagRepository;

    @Mock
    private UserPreferenceRepository userPreferenceRepository;

    @Mock
    private UserAllergyRepository userAllergyRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserListRepository userListRepository;

    @Mock
    private UserListItemRepository userListItemRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private MediaAssetMapper mediaAssetMapper;

    @Mock
    private UserProfileMapper userProfileMapper;

    @Mock
    private ProfileTagMapper profileTagMapper;

    @Mock
    private UserPreferenceMapper userPreferenceMapper;

    @Mock
    private AllergyMapper allergyMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private UserListMapper userListMapper;

    private UserFullProfileService service;

    @BeforeEach
    void setUp() {
        service = new UserFullProfileService(userRepository, mediaAssetRepository, userProfileRepository,
                userProfileTagRepository, userPreferenceRepository, userAllergyRepository, favoriteRepository,
                userListRepository, userListItemRepository, userMapper, mediaAssetMapper, userProfileMapper,
                profileTagMapper, userPreferenceMapper, allergyMapper, productMapper, userListMapper);
    }

    @Test
    void missingUserGivesNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByUserId(USER_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void userProfileReadsAllParts() {
        UserAllergy highAllergy = new UserAllergy();
        highAllergy.setSeverity(RiskLevel.HIGH);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(new User()));
        when(mediaAssetRepository.findByUserIdAndPurposeAndStatusIn(
                USER_ID, MediaPurpose.AVATAR, MediaAssetRepository.LIVE_STATUSES)).thenReturn(Optional.empty());
        when(userProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(userProfileTagRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(userPreferenceRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(userAllergyRepository.findByUserId(USER_ID)).thenReturn(List.of(highAllergy));
        when(favoriteRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(userListRepository.findByUserId(USER_ID)).thenReturn(List.of());

        UserFullProfileResponse response = service.findByUserId(USER_ID);

        assertThat(response.avatar()).isNull();
        assertThat(response.profile()).isNull();
        assertThat(response.tags()).isEmpty();
        assertThat(response.preferences()).isNull();
        assertThat(response.allergies()).containsExactly(new UserAllergyDetailResponse(null, RiskLevel.HIGH));
        assertThat(response.favorites()).isEmpty();
        assertThat(response.lists()).isEmpty();
        verifyNoInteractions(userListItemRepository);
    }
}
