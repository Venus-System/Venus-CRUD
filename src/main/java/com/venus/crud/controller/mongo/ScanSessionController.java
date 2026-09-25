package com.venus.crud.controller.mongo;

import com.venus.crud.dto.mongo.request.ScanSessionRequest;
import com.venus.crud.dto.mongo.response.ScanSessionResponse;
import com.venus.crud.dto.mongo.response.ScanUploadSignaturesResponse;
import com.venus.crud.entity.enums.ScanStatus;
import com.venus.crud.service.mongo.ScanCloudinaryService;
import com.venus.crud.service.mongo.ScanSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/scan-sessions")
@Tag(name = "Sessões de Scan", description = "Sessões de leitura de rótulo guardadas no MongoDB, com o agregado do resultado.")
public class ScanSessionController {

    private final ScanSessionService scanSessionService;
    private final ScanCloudinaryService scanCloudinaryService;

    public ScanSessionController(ScanSessionService scanSessionService, ScanCloudinaryService scanCloudinaryService) {
        this.scanSessionService = scanSessionService;
        this.scanCloudinaryService = scanCloudinaryService;
    }

    @Operation(operationId = "scanSessionFindAll", summary = "Lista as sessões de scan")
    @GetMapping
    public ResponseEntity<Slice<ScanSessionResponse>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scanSessionService.findAll(pageable));
    }

    @Operation(operationId = "scanSessionFindById", summary = "Busca a sessão de scan por id")
    @GetMapping("/{id}")
    public ResponseEntity<ScanSessionResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(scanSessionService.findById(id));
    }

    @Operation(operationId = "scanSessionFindByStatus", summary = "Lista as sessões de scan por status")
    @GetMapping("/status/{status}")
    public ResponseEntity<Slice<ScanSessionResponse>> findByStatus(
            @PathVariable ScanStatus status, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scanSessionService.findByStatus(status, pageable));
    }

    @Operation(operationId = "scanSessionFindByDeviceId", summary = "Lista as sessões de scan de um dispositivo")
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Slice<ScanSessionResponse>> findByDeviceId(
            @Parameter(description = "Identificador do aparelho que originou o scan.")
            @PathVariable String deviceId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(scanSessionService.findByDeviceId(deviceId, pageable));
    }

    @Operation(
            operationId = "scanSessionUploadSignatures",
            summary = "Gera as assinaturas para o app subir as fotos do scan no Cloudinary",
            description = "Devolve uma assinatura para a frente e outra para o verso, com os public_id scans/{scanId}/front "
                    + "e scans/{scanId}/back. O app sobe as fotos direto no Cloudinary mandando exatamente esses "
                    + "parâmetros e depois envia o scan com a referência das fotos. A assinatura vale por 1 hora.")
    @GetMapping("/upload-signatures")
    public ResponseEntity<ScanUploadSignaturesResponse> uploadSignatures(
            @Parameter(description = "Identificador do scan gerado pelo app.")
            @RequestParam UUID scanId) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(scanCloudinaryService.signUploads(scanId));
    }

    @Operation(
            operationId = "scanSessionCreate",
            summary = "Cadastra uma sessão de scan",
            description = "Grava o scan com status PENDING_REVIEW e compara cada ingrediente com o catálogo. "
                    + "Reenviar o mesmo scanId não cria outro scan: devolve o que já existe, com o mesmo id.")
    @PostMapping
    public ResponseEntity<ScanSessionResponse> create(@Valid @RequestBody ScanSessionRequest request) {
        ScanSessionResponse created = scanSessionService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
