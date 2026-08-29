package com.venus.crud.service.jpa.shared;

import com.venus.crud.dto.jpa.patch.shared.ProfileTagPatchRequest;
import com.venus.crud.dto.jpa.request.shared.ProfileTagRequest;
import com.venus.crud.dto.jpa.response.shared.ProfileTagResponse;
import com.venus.crud.entity.enums.ProfileTagCategory;
import com.venus.crud.entity.shared.ProfileTag;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.shared.ProfileTagMapper;
import com.venus.crud.repository.jpa.shared.ProfileTagRepository;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ProfileTagService {

    private static final Logger log = LoggerFactory.getLogger(ProfileTagService.class);

    private final ProfileTagRepository profileTagRepository;
    private final ProfileTagMapper profileTagMapper;

    public ProfileTagService(ProfileTagRepository profileTagRepository, ProfileTagMapper profileTagMapper) {
        this.profileTagRepository = profileTagRepository;
        this.profileTagMapper = profileTagMapper;
    }

    @Transactional(readOnly = true)
    public List<ProfileTagResponse> findAll() {
        return executeOrFail(profileTagRepository::findAll, "Falha ao consultar tags de perfil no banco de dados").stream()
                .map(profileTagMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProfileTagResponse findById(Long id) {
        return profileTagMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<ProfileTagResponse> search(ProfileTagCategory category, Pageable pageable) {
        Slice<ProfileTag> result = category != null
                ? executeOrFail(() -> profileTagRepository.findByCategory(category, pageable), "Falha ao consultar tags de perfil por categoria")
                : executeOrFail(() -> profileTagRepository.findAllBy(pageable), "Falha ao consultar tags de perfil");

        return result.map(profileTagMapper::toResponse);
    }

    @Transactional
    public ProfileTagResponse create(ProfileTagRequest request) {
        ensureNameAvailable(request.name(), null);
        ensureSlugAvailable(request.slug(), null);

        ProfileTag profileTag = profileTagMapper.toEntity(request);
        ProfileTag saved = executeOrFail(() -> profileTagRepository.save(profileTag), "Falha ao criar tag de perfil no banco de dados");
        return profileTagMapper.toResponse(saved);
    }

    @Transactional
    public ProfileTagResponse update(Long id, ProfileTagRequest request) {
        ProfileTag profileTag = getOrThrow(id);
        ensureNameAvailable(request.name(), id);
        ensureSlugAvailable(request.slug(), id);

        profileTagMapper.updateEntity(request, profileTag);
        ProfileTag saved = executeOrFail(() -> profileTagRepository.save(profileTag), "Falha ao atualizar tag de perfil no banco de dados");
        return profileTagMapper.toResponse(saved);
    }

    @Transactional
    public ProfileTagResponse patch(Long id, ProfileTagPatchRequest request) {
        ProfileTag profileTag = getOrThrow(id);
        if (StringUtils.hasText(request.name())) {
            ensureNameAvailable(request.name(), id);
        }
        if (StringUtils.hasText(request.slug())) {
            ensureSlugAvailable(request.slug(), id);
        }

        profileTagMapper.patchEntity(request, profileTag);
        ProfileTag saved = executeOrFail(() -> profileTagRepository.save(profileTag), "Falha ao atualizar tag de perfil no banco de dados");
        return profileTagMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        ProfileTag profileTag = getOrThrow(id);
        executeOrFail(() -> {
            profileTagRepository.delete(profileTag);
            return null;
        }, "Falha ao remover tag de perfil no banco de dados");
    }

    private ProfileTag getOrThrow(Long id) {
        Optional<ProfileTag> profileTag = executeOrFail(() -> profileTagRepository.findById(id), "Falha ao consultar tag de perfil no banco de dados");
        return profileTag.orElseThrow(() -> new ResourceNotFoundException("Tag de perfil nao encontrada com id " + id));
    }

    private void ensureNameAvailable(String name, Long excludeId) {
        boolean inUse = executeOrFail(() -> profileTagRepository.findByName(name), "Falha ao verificar nome da tag de perfil")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe uma tag de perfil com o nome " + name);
        }
    }

    private void ensureSlugAvailable(String slug, Long excludeId) {
        boolean inUse = executeOrFail(() -> profileTagRepository.findBySlug(slug), "Falha ao verificar slug da tag de perfil")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe uma tag de perfil com o slug " + slug);
        }
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
