package com.venus.crud.service.jpa.scoring;

import com.venus.crud.dto.jpa.patch.scoring.ScoreCategoryPatchRequest;
import com.venus.crud.dto.jpa.request.scoring.ScoreCategoryRequest;
import com.venus.crud.dto.jpa.response.scoring.ScoreCategoryResponse;
import com.venus.crud.entity.scoring.ScoreCategory;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.scoring.ScoreCategoryMapper;
import com.venus.crud.repository.jpa.scoring.ScoreCategoryRepository;
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
public class ScoreCategoryService {

    private static final Logger log = LoggerFactory.getLogger(ScoreCategoryService.class);

    private final ScoreCategoryRepository scoreCategoryRepository;
    private final ScoreCategoryMapper scoreCategoryMapper;

    public ScoreCategoryService(ScoreCategoryRepository scoreCategoryRepository, ScoreCategoryMapper scoreCategoryMapper) {
        this.scoreCategoryRepository = scoreCategoryRepository;
        this.scoreCategoryMapper = scoreCategoryMapper;
    }

    @Transactional(readOnly = true)
    public List<ScoreCategoryResponse> findAll() {
        return executeOrFail(scoreCategoryRepository::findAll, "Falha ao consultar categorias de score no banco de dados").stream()
                .map(scoreCategoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ScoreCategoryResponse findById(Long id) {
        return scoreCategoryMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<ScoreCategoryResponse> search(String name, Pageable pageable) {
        Slice<ScoreCategory> result = StringUtils.hasText(name)
                ? executeOrFail(() -> scoreCategoryRepository.findByNameContainingIgnoreCase(name, pageable),
                        "Falha ao consultar categorias de score por nome")
                : executeOrFail(() -> scoreCategoryRepository.findAllBy(pageable), "Falha ao consultar categorias de score");

        return result.map(scoreCategoryMapper::toResponse);
    }

    @Transactional
    public ScoreCategoryResponse create(ScoreCategoryRequest request) {
        ensureNameAvailable(request.name(), null);

        ScoreCategory scoreCategory = scoreCategoryMapper.toEntity(request);
        ScoreCategory saved = executeOrFail(() -> scoreCategoryRepository.save(scoreCategory),
                "Falha ao criar categoria de score no banco de dados");
        return scoreCategoryMapper.toResponse(saved);
    }

    @Transactional
    public ScoreCategoryResponse update(Long id, ScoreCategoryRequest request) {
        ScoreCategory scoreCategory = getOrThrow(id);
        ensureNameAvailable(request.name(), id);

        scoreCategoryMapper.updateEntity(request, scoreCategory);
        ScoreCategory saved = executeOrFail(() -> scoreCategoryRepository.save(scoreCategory),
                "Falha ao atualizar categoria de score no banco de dados");
        return scoreCategoryMapper.toResponse(saved);
    }

    @Transactional
    public ScoreCategoryResponse patch(Long id, ScoreCategoryPatchRequest request) {
        ScoreCategory scoreCategory = getOrThrow(id);
        if (StringUtils.hasText(request.name())) {
            ensureNameAvailable(request.name(), id);
        }

        scoreCategoryMapper.patchEntity(request, scoreCategory);
        ScoreCategory saved = executeOrFail(() -> scoreCategoryRepository.save(scoreCategory),
                "Falha ao atualizar categoria de score no banco de dados");
        return scoreCategoryMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        ScoreCategory scoreCategory = getOrThrow(id);
        executeOrFail(() -> {
            scoreCategoryRepository.delete(scoreCategory);
            return null;
        }, "Falha ao remover categoria de score no banco de dados");
    }

    private ScoreCategory getOrThrow(Long id) {
        Optional<ScoreCategory> scoreCategory = executeOrFail(() -> scoreCategoryRepository.findById(id),
                "Falha ao consultar categoria de score no banco de dados");
        return scoreCategory.orElseThrow(() -> new ResourceNotFoundException("Categoria de score nao encontrada com id " + id));
    }

    private void ensureNameAvailable(String name, Long excludeId) {
        boolean inUse = executeOrFail(() -> scoreCategoryRepository.findByNameIgnoreCase(name), "Falha ao verificar nome da categoria de score")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe uma categoria de score com o nome " + name);
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