package com.venus.crud.controller.jpa.media;

import com.venus.crud.dto.jpa.response.media.MediaAssetResponse;
import com.venus.crud.service.jpa.media.MediaAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/user-lists")
@Tag(name = "Listas do Usuário", description = "Listas criadas pelo usuário e os produtos dentro delas.")
public class UserListCoverController {

    private final MediaAssetService mediaAssetService;

    public UserListCoverController(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @Operation(operationId = "userListCoverFindCover", summary = "Busca a foto de capa da lista")
    @PreAuthorize("@ownership.canAccessUserList(#id)")
    @GetMapping("/{id}/cover")
    public ResponseEntity<MediaAssetResponse> findCover(@PathVariable Long id) {
        return ResponseEntity.ok(mediaAssetService.findListCover(id));
    }

    @Operation(
            operationId = "userListCoverUploadCover",
            summary = "Envia a foto de capa da lista",
            description = "Envio `multipart/form-data` no campo `file`. Uma capa nova substitui a anterior. Os tipos aceitos, "
                    + "o tamanho máximo e as dimensões máximas são os mesmos do avatar e são configurados por ambiente; "
                    + "quando o arquivo é recusado, a mensagem do erro 400 informa qual limite não foi respeitado.")
    @PreAuthorize("@ownership.canAccessUserList(#id)")
    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaAssetResponse> uploadCover(@PathVariable Long id, @RequestParam MultipartFile file) {
        MediaAssetResponse createdCover = mediaAssetService.uploadListCover(id, file);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).body(createdCover);
    }

    @Operation(operationId = "userListCoverDeleteCover", summary = "Remove a foto de capa da lista")
    @PreAuthorize("@ownership.canAccessUserList(#id)")
    @DeleteMapping("/{id}/cover")
    public ResponseEntity<Void> deleteCover(@PathVariable Long id) {
        mediaAssetService.deleteListCover(id);
        return ResponseEntity.noContent().build();
    }
}
