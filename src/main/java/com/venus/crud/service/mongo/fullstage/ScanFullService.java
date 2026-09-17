package com.venus.crud.service.mongo.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.AnalysisResultFullResponse;
import com.venus.crud.dto.jpa.response.fullstage.ProductFullResponse;
import com.venus.crud.dto.jpa.response.product.ProductVersionResponse;
import com.venus.crud.dto.mongo.response.ScanSessionResponse;
import com.venus.crud.dto.mongo.response.fullstage.ScanFullResponse;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.product.ProductVersionMapper;
import com.venus.crud.repository.jpa.product.ProductVersionRepository;
import com.venus.crud.service.jpa.fullstage.AnalysisResultFullService;
import com.venus.crud.service.jpa.fullstage.ProductFullService;
import com.venus.crud.service.mongo.ScanSessionService;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScanFullService {

    private static final Logger log = LoggerFactory.getLogger(ScanFullService.class);

    private final ScanSessionService scanSessionService;
    private final AnalysisResultFullService analysisResultFullService;
    private final ProductFullService productFullService;
    private final ProductVersionRepository productVersionRepository;
    private final ProductVersionMapper productVersionMapper;

    public ScanFullService(ScanSessionService scanSessionService, AnalysisResultFullService analysisResultFullService,
            ProductFullService productFullService, ProductVersionRepository productVersionRepository,
            ProductVersionMapper productVersionMapper) {
        this.scanSessionService = scanSessionService;
        this.analysisResultFullService = analysisResultFullService;
        this.productFullService = productFullService;
        this.productVersionRepository = productVersionRepository;
        this.productVersionMapper = productVersionMapper;
    }

    @Transactional(readOnly = true)
    public ScanFullResponse findByScanSessionIdAndAnalysisResultId(String scanSessionId, Long analysisResultId) {
        ScanSessionResponse scan = scanSessionService.findById(scanSessionId);
        AnalysisResultFullResponse analysis = analysisResultFullService.findById(analysisResultId);

        Long productVersionId = analysis.analysis().productVersionId();
        ProductVersion productVersion = executeOrFail(() -> productVersionRepository.findById(productVersionId),
                "Falha ao consultar versao de produto analisada")
                .orElseThrow(() -> new ResourceNotFoundException("Versao de produto nao encontrada com id " + productVersionId));

        ProductVersionResponse analyzedVersion = productVersionMapper.toResponse(productVersion);
        ProductFullResponse product = productFullService.findById(productVersion.getProduct().getId());

        boolean productChangedSinceScan = product.currentVersion() == null
                || !analyzedVersion.id().equals(product.currentVersion().id());

        return new ScanFullResponse(scan, analysis, product, analyzedVersion, productChangedSinceScan);
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
