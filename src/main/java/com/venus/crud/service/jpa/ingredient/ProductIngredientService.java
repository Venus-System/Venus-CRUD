package com.venus.crud.service.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.ProductIngredientPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.ProductIngredientRequest;
import com.venus.crud.dto.jpa.response.ingredient.ProductIngredientResponse;
import com.venus.crud.entity.ingredient.ProductIngredient;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.ingredient.ProductIngredientMapper;
import com.venus.crud.repository.jpa.ingredient.ProductIngredientRepository;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductIngredientService {

    private static final Logger log = LoggerFactory.getLogger(ProductIngredientService.class);

    private final ProductIngredientRepository productIngredientRepository;
    private final ProductIngredientMapper productIngredientMapper;

    public ProductIngredientService(ProductIngredientRepository productIngredientRepository,
            ProductIngredientMapper productIngredientMapper) {
        this.productIngredientRepository = productIngredientRepository;
        this.productIngredientMapper = productIngredientMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductIngredientResponse> findAll() {
        return executeOrFail(productIngredientRepository::findAll, "Falha ao consultar ingredientes de produto no banco de dados").stream()
                .map(productIngredientMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductIngredientResponse> findByProductVersionId(Long productVersionId) {
        return executeOrFail(() -> productIngredientRepository.findByProductVersionIdOrderByPosition(productVersionId),
                "Falha ao consultar ingredientes da versao de produto").stream()
                .map(productIngredientMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<ProductIngredientResponse> findByIngredientId(Long ingredientId, Pageable pageable) {
        return executeOrFail(() -> productIngredientRepository.findByIngredientId(ingredientId, pageable),
                "Falha ao consultar produtos que usam o ingrediente")
                .map(productIngredientMapper::toResponse);
    }

    @Transactional
    public ProductIngredientResponse create(ProductIngredientRequest request) {
        ensureIngredientNotAssigned(request.productVersionId(), request.ingredientId());

        ProductIngredient productIngredient = productIngredientMapper.toEntity(request);
        ProductIngredient saved = executeOrFail(() -> productIngredientRepository.save(productIngredient),
                "Falha ao associar ingrediente a versao de produto");
        return productIngredientMapper.toResponse(saved);
    }

    @Transactional
    public ProductIngredientResponse patch(Long productVersionId, Long ingredientId, ProductIngredientPatchRequest request) {
        ProductIngredient productIngredient = getOrThrow(productVersionId, ingredientId);
        productIngredientMapper.patchEntity(request, productIngredient);

        ProductIngredient saved = executeOrFail(() -> productIngredientRepository.save(productIngredient),
                "Falha ao atualizar ingrediente da versao de produto");
        return productIngredientMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long productVersionId, Long ingredientId) {
        getOrThrow(productVersionId, ingredientId);
        executeOrFail(() -> {
            productIngredientRepository.deleteByProductVersionIdAndIngredientId(productVersionId, ingredientId);
            return null;
        }, "Falha ao remover ingrediente da versao de produto");
    }

    private ProductIngredient getOrThrow(Long productVersionId, Long ingredientId) {
        var productIngredient = executeOrFail(
                () -> productIngredientRepository.findByProductVersionIdAndIngredientId(productVersionId, ingredientId),
                "Falha ao consultar ingrediente da versao de produto");
        return productIngredient.orElseThrow(() -> new ResourceNotFoundException(
                "O ingrediente " + ingredientId + " nao esta associado a versao de produto " + productVersionId));
    }

    private void ensureIngredientNotAssigned(Long productVersionId, Long ingredientId) {
        boolean exists = executeOrFail(
                () -> productIngredientRepository.existsByProductVersionIdAndIngredientId(productVersionId, ingredientId),
                "Falha ao verificar ingrediente ja associado");
        if (exists) {
            throw new DuplicateResourceException(
                    "O ingrediente " + ingredientId + " ja esta associado a versao de produto " + productVersionId);
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