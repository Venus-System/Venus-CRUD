package com.venus.crud.controller.jpa.media;

import com.venus.crud.dto.jpa.response.media.MediaAssetResponse;
import com.venus.crud.service.jpa.media.MediaAssetService;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/users")
public class UserAvatarController {

    private final MediaAssetService mediaAssetService;

    public UserAvatarController(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<MediaAssetResponse> findAvatar(@PathVariable Long userId) {
        return ResponseEntity.ok(mediaAssetService.findAvatar(userId));
    }

    @PostMapping(value = "/{userId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaAssetResponse> uploadAvatar(@PathVariable Long userId, @RequestParam MultipartFile file) {
        MediaAssetResponse created = mediaAssetService.uploadAvatar(userId, file);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<Void> deleteAvatar(@PathVariable Long userId) {
        mediaAssetService.deleteAvatar(userId);
        return ResponseEntity.noContent().build();
    }
}
