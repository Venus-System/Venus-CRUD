package com.venus.crud.service.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.UserAllergyDetailResponse;
import com.venus.crud.dto.jpa.response.fullstage.UserFullProfileResponse;
import com.venus.crud.dto.jpa.response.fullstage.UserListWithItemsResponse;
import com.venus.crud.dto.jpa.response.user.UserPreferenceResponse;
import com.venus.crud.dto.jpa.response.user.UserProfileResponse;
import com.venus.crud.entity.user.User;
import com.venus.crud.entity.user.UserList;
import com.venus.crud.entity.user.UserListItem;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.shared.ProfileTagMapper;
import com.venus.crud.mapper.jpa.user.AllergyMapper;
import com.venus.crud.mapper.jpa.user.FavoriteMapper;
import com.venus.crud.mapper.jpa.user.UserListItemMapper;
import com.venus.crud.mapper.jpa.user.UserListMapper;
import com.venus.crud.mapper.jpa.user.UserMapper;
import com.venus.crud.mapper.jpa.user.UserPreferenceMapper;
import com.venus.crud.mapper.jpa.user.UserProfileMapper;
import com.venus.crud.repository.jpa.user.FavoriteRepository;
import com.venus.crud.repository.jpa.user.UserAllergyRepository;
import com.venus.crud.repository.jpa.user.UserListItemRepository;
import com.venus.crud.repository.jpa.user.UserListRepository;
import com.venus.crud.repository.jpa.user.UserPreferenceRepository;
import com.venus.crud.repository.jpa.user.UserProfileRepository;
import com.venus.crud.repository.jpa.user.UserProfileTagRepository;
import com.venus.crud.repository.jpa.user.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserFullProfileService {

    private static final Logger log = LoggerFactory.getLogger(UserFullProfileService.class);

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileTagRepository userProfileTagRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserAllergyRepository userAllergyRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserListRepository userListRepository;
    private final UserListItemRepository userListItemRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final ProfileTagMapper profileTagMapper;
    private final UserPreferenceMapper userPreferenceMapper;
    private final AllergyMapper allergyMapper;
    private final FavoriteMapper favoriteMapper;
    private final UserListMapper userListMapper;
    private final UserListItemMapper userListItemMapper;

    public UserFullProfileService(UserRepository userRepository, UserProfileRepository userProfileRepository,
            UserProfileTagRepository userProfileTagRepository, UserPreferenceRepository userPreferenceRepository,
            UserAllergyRepository userAllergyRepository, FavoriteRepository favoriteRepository,
            UserListRepository userListRepository, UserListItemRepository userListItemRepository,
            UserMapper userMapper, UserProfileMapper userProfileMapper, ProfileTagMapper profileTagMapper,
            UserPreferenceMapper userPreferenceMapper, AllergyMapper allergyMapper, FavoriteMapper favoriteMapper,
            UserListMapper userListMapper, UserListItemMapper userListItemMapper) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userProfileTagRepository = userProfileTagRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.userAllergyRepository = userAllergyRepository;
        this.favoriteRepository = favoriteRepository;
        this.userListRepository = userListRepository;
        this.userListItemRepository = userListItemRepository;
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
        this.profileTagMapper = profileTagMapper;
        this.userPreferenceMapper = userPreferenceMapper;
        this.allergyMapper = allergyMapper;
        this.favoriteMapper = favoriteMapper;
        this.userListMapper = userListMapper;
        this.userListItemMapper = userListItemMapper;
    }

    @Transactional(readOnly = true)
    public UserFullProfileResponse findByUserId(Long userId) {
        User user = executeOrFail(() -> userRepository.findById(userId), "Falha ao consultar usuario no banco de dados")
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id " + userId));

        UserProfileResponse profile = executeOrFail(() -> userProfileRepository.findByUserId(userId), "Falha ao consultar perfil de usuario no banco de dados")
                .map(userProfileMapper::toResponse)
                .orElse(null);

        var tags = executeOrFail(() -> userProfileTagRepository.findByUserId(userId), "Falha ao consultar tags do usuario")
                .stream()
                .map(userProfileTag -> profileTagMapper.toResponse(userProfileTag.getProfileTag()))
                .toList();

        UserPreferenceResponse preferences = executeOrFail(() -> userPreferenceRepository.findByUserId(userId), "Falha ao consultar preferencias de usuario")
                .map(userPreferenceMapper::toResponse)
                .orElse(null);

        var allergies = executeOrFail(() -> userAllergyRepository.findByUserId(userId), "Falha ao consultar alergias do usuario")
                .stream()
                .map(userAllergy -> new UserAllergyDetailResponse(allergyMapper.toResponse(userAllergy.getAllergy()), userAllergy.getSeverity()))
                .toList();

        var favorites = executeOrFail(() -> favoriteRepository.findByUserId(userId), "Falha ao consultar favoritos do usuario")
                .stream()
                .map(favoriteMapper::toResponse)
                .toList();

        var lists = buildLists(userId);

        return new UserFullProfileResponse(userMapper.toResponse(user), profile, tags, preferences, allergies, favorites, lists);
    }

    private List<UserListWithItemsResponse> buildLists(Long userId) {
        List<UserList> userLists = executeOrFail(() -> userListRepository.findByUserId(userId), "Falha ao consultar listas do usuario");
        List<Long> userListIds = userLists.stream().map(UserList::getId).toList();

        Map<Long, List<UserListItem>> itemsByListId = userListIds.isEmpty()
                ? Map.of()
                : executeOrFail(() -> userListItemRepository.findByUserListIdInOrderByPositionOrder(userListIds), "Falha ao consultar itens das listas")
                        .stream()
                        .collect(Collectors.groupingBy(item -> item.getUserList().getId()));

        return userLists.stream()
                .map(userList -> new UserListWithItemsResponse(
                        userListMapper.toResponse(userList),
                        itemsByListId.getOrDefault(userList.getId(), List.of()).stream().map(userListItemMapper::toResponse).toList()))
                .toList();
    }

    private <T> T executeOrFail(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException ex) {
            log.warn("Violacao de integridade de dados: {}", ex.getMessage());
            throw new DuplicateResourceException("Os dados informados conflitam com um registro existente.");
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw new ServiceUnavailableException(errorMessage + ". Tente novamente mais tarde.", ex);
        }
    }
}