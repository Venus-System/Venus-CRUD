package com.venus.crud.service.user;

import com.venus.crud.dto.request.user.AllergyPatchRequest;
import com.venus.crud.dto.request.user.AllergyRequest;
import com.venus.crud.dto.response.user.AllergyResponse;
import com.venus.crud.entity.enums.AllergyType;
import com.venus.crud.entity.user.Allergy;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.user.AllergyMapper;
import com.venus.crud.repository.jpa.user.AllergyRepository;
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
public class AllergyService {

    private static final Logger log = LoggerFactory.getLogger(AllergyService.class);

    private final AllergyRepository allergyRepository;
    private final AllergyMapper allergyMapper;

    public AllergyService(AllergyRepository allergyRepository, AllergyMapper allergyMapper) {
        this.allergyRepository = allergyRepository;
        this.allergyMapper = allergyMapper;
    }

    @Transactional(readOnly = true)
    public List<AllergyResponse> findAll() {
        return executeOrFail(allergyRepository::findAll, "Falha ao consultar alergias no banco de dados").stream()
                .map(allergyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AllergyResponse findById(Long id) {
        return allergyMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<AllergyResponse> search(AllergyType allergyType, Pageable pageable) {
        Slice<Allergy> result = allergyType != null
                ? executeOrFail(() -> allergyRepository.findByAllergyType(allergyType, pageable), "Falha ao consultar alergias por tipo")
                : executeOrFail(() -> allergyRepository.findAllBy(pageable), "Falha ao consultar alergias");

        return result.map(allergyMapper::toResponse);
    }

    @Transactional
    public AllergyResponse create(AllergyRequest request) {
        ensureAllergyNameAvailable(request.allergyName(), null);

        Allergy allergy = allergyMapper.toEntity(request);
        Allergy saved = executeOrFail(() -> allergyRepository.save(allergy), "Falha ao criar alergia no banco de dados");
        return allergyMapper.toResponse(saved);
    }

    @Transactional
    public AllergyResponse update(Long id, AllergyRequest request) {
        Allergy allergy = getOrThrow(id);
        ensureAllergyNameAvailable(request.allergyName(), id);

        allergyMapper.updateEntity(request, allergy);
        Allergy saved = executeOrFail(() -> allergyRepository.save(allergy), "Falha ao atualizar alergia no banco de dados");
        return allergyMapper.toResponse(saved);
    }

    @Transactional
    public AllergyResponse patch(Long id, AllergyPatchRequest request) {
        Allergy allergy = getOrThrow(id);
        if (StringUtils.hasText(request.allergyName())) {
            ensureAllergyNameAvailable(request.allergyName(), id);
        }

        allergyMapper.patchEntity(request, allergy);
        Allergy saved = executeOrFail(() -> allergyRepository.save(allergy), "Falha ao atualizar alergia no banco de dados");
        return allergyMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Allergy allergy = getOrThrow(id);
        executeOrFail(() -> {
            allergyRepository.delete(allergy);
            return null;
        }, "Falha ao remover alergia no banco de dados");
    }

    private Allergy getOrThrow(Long id) {
        Optional<Allergy> allergy = executeOrFail(() -> allergyRepository.findById(id), "Falha ao consultar alergia no banco de dados");
        return allergy.orElseThrow(() -> new ResourceNotFoundException("Alergia nao encontrada com id " + id));
    }

    private void ensureAllergyNameAvailable(String allergyName, Long excludeId) {
        boolean inUse = executeOrFail(() -> allergyRepository.findByAllergyName(allergyName), "Falha ao verificar nome da alergia")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe uma alergia com o nome " + allergyName);
        }
    }

    private <T> T executeOrFail(Supplier<T> action, String errorMessage) {
        try{
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