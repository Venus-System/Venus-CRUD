package com.venus.crud.controller.jpa.media;

import com.venus.crud.dto.jpa.patch.media.MediaAssetPatchRequest;
import com.venus.crud.dto.jpa.response.media.MediaAssetResponse;
import com.venus.crud.service.jpa.media.MediaAssetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/media")
public class MediaAssetController {

    private final MediaAssetService mediaAssetService;

    public MediaAssetController(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @PatchMapping("/{mediaAssetId}")
    public ResponseEntity<MediaAssetResponse> patch(@PathVariable Long mediaAssetId,
            @Valid @RequestBody MediaAssetPatchRequest request) {
        return ResponseEntity.ok(mediaAssetService.patch(mediaAssetId, request));
    }

    @DeleteMapping("/{mediaAssetId}")
    public ResponseEntity<Void> delete(@PathVariable Long mediaAssetId) {
        mediaAssetService.delete(mediaAssetId);
        return ResponseEntity.noContent().build();
    }
}
