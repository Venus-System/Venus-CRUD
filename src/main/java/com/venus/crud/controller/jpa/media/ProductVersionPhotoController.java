package com.venus.crud.controller.jpa.media;

import com.venus.crud.dto.jpa.response.media.MediaAssetResponse;
import com.venus.crud.service.jpa.media.MediaAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/product-versions")
@Tag(name = "Versões de Produto", description = "Versões de formulação de um produto e suas fotos.")
public class ProductVersionPhotoController {

    private final MediaAssetService mediaAssetService;

    public ProductVersionPhotoController(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @Operation(operationId = "productVersionPhotoFindPhotos", summary = "Lista as fotos de uma versão de produto")
    @GetMapping("/{productVersionId}/photos")
    public ResponseEntity<List<MediaAssetResponse>> findPhotos(@PathVariable Long productVersionId) {
        return ResponseEntity.ok(mediaAssetService.findProductPhotos(productVersionId));
    }

    @Operation(
            operationId = "productVersionPhotoUploadPhoto",
            summary = "Envia uma foto para a versão de produto",
            description = "Envio `multipart/form-data` no campo `file`. Os tipos aceitos, o tamanho máximo e as dimensões "
                    + "máximas são configurados por ambiente; quando o arquivo é recusado, a mensagem do erro 400 "
                    + "informa qual limite não foi respeitado.")
    @PostMapping(value = "/{productVersionId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaAssetResponse> uploadPhoto(
            @PathVariable Long productVersionId,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String altText,
            @RequestParam(required = false) Integer sortOrder) {
        MediaAssetResponse created = mediaAssetService.uploadProductPhoto(productVersionId, file, altText, sortOrder);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/media/{mediaAssetId}")
                .buildAndExpand(created.mediaAssetId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
