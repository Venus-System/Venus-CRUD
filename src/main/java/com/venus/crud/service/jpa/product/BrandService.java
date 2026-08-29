package com.venus.crud.service.jpa.product;

import com.venus.crud.dto.jpa.patch.product.BrandPatchRequest;
import com.venus.crud.dto.jpa.request.product.BrandRequest;
import com.venus.crud.dto.jpa.response.product.BrandResponse;
import com.venus.crud.entity.product.Brand;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.product.BrandMapper;
import com.venus.crud.repository.jpa.product.BrandRepository;
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
public class BrandService {

    private static final Logger log = LoggerFactory.getLogger(BrandService.class);

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandService(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> findAll() {
        return executeOrFail(brandRepository::findAll, "Falha ao consultar marcas no banco de dados").stream()
                .map(brandMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BrandResponse findById(Long id) {
        return brandMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<BrandResponse> search(String name, String country, Boolean hasCrueltyFreeClaim,
            Boolean hasVeganClaim, Boolean isBrazilian, Pageable pageable) {
        Slice<Brand> result;
        if (StringUtils.hasText(name)) {
            result = executeOrFail(() -> brandRepository.findByNameContainingIgnoreCase(name, pageable), "Falha ao consultar marcas por nome");
        } else if (StringUtils.hasText(country)) {
            result = executeOrFail(() -> brandRepository.findByCountry(country, pageable), "Falha ao consultar marcas por pais");
        } else if (Boolean.TRUE.equals(hasCrueltyFreeClaim)) {
            result = executeOrFail(() -> brandRepository.findByHasCrueltyFreeClaimTrue(pageable), "Falha ao consultar marcas cruelty-free");
        } else if (Boolean.TRUE.equals(hasVeganClaim)) {
            result = executeOrFail(() -> brandRepository.findByHasVeganClaimTrue(pageable), "Falha ao consultar marcas veganas");
        } else if (Boolean.TRUE.equals(isBrazilian)) {
            result = executeOrFail(() -> brandRepository.findByIsBrazilianTrue(pageable), "Falha ao consultar marcas brasileiras");
        } else {
            result = executeOrFail(() -> brandRepository.findAllBy(pageable), "Falha ao consultar marcas");
        }

        return result.map(brandMapper::toResponse);
    }

    @Transactional
    public BrandResponse create(BrandRequest request) {
        ensureNameAvailable(request.name(), null);

        Brand brand = brandMapper.toEntity(request);
        Brand saved = executeOrFail(() -> brandRepository.save(brand), "Falha ao criar marca no banco de dados");
        return brandMapper.toResponse(saved);
    }

    @Transactional
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = getOrThrow(id);
        ensureNameAvailable(request.name(), id);

        brandMapper.updateEntity(request, brand);
        Brand saved = executeOrFail(() -> brandRepository.save(brand), "Falha ao atualizar marca no banco de dados");
        return brandMapper.toResponse(saved);
    }

    @Transactional
    public BrandResponse patch(Long id, BrandPatchRequest request) {
        Brand brand = getOrThrow(id);
        if (StringUtils.hasText(request.name())) {
            ensureNameAvailable(request.name(), id);
        }

        brandMapper.patchEntity(request, brand);
        Brand saved = executeOrFail(() -> brandRepository.save(brand), "Falha ao atualizar marca no banco de dados");
        return brandMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Brand brand = getOrThrow(id);
        executeOrFail(() -> {
            brandRepository.delete(brand);
            return null;
        }, "Falha ao remover marca no banco de dados");
    }

    private Brand getOrThrow(Long id) {
        Optional<Brand> brand = executeOrFail(() -> brandRepository.findById(id), "Falha ao consultar marca no banco de dados");
        return brand.orElseThrow(() -> new ResourceNotFoundException("Marca nao encontrada com id " + id));
    }

    private void ensureNameAvailable(String name, Long excludeId) {
        boolean inUse = executeOrFail(() -> brandRepository.findByNameIgnoreCase(name), "Falha ao verificar nome da marca")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (inUse) {
            throw new DuplicateResourceException("Ja existe uma marca com o nome " + name);
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
