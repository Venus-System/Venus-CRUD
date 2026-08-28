package com.venus.crud.controller.user;

import com.venus.crud.dto.request.user.UserProfileTagRequest;
import com.venus.crud.dto.response.user.UserProfileTagResponse;
import com.venus.crud.service.user.UserProfileTagService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/user-profile-tags")
public class UserProfileTagController {

    private final UserProfileTagService userProfileTagService;

    public UserProfileTagController(UserProfileTagService userProfileTagService) {
        this.userProfileTagService = userProfileTagService;
    }

    @GetMapping
    public ResponseEntity<List<UserProfileTagResponse>> findAll() {
        return ResponseEntity.ok(userProfileTagService.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Slice<UserProfileTagResponse>> findByUserId(
            @PathVariable Long userId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userProfileTagService.findByUserId(userId, pageable));
    }

    @GetMapping("/profile-tag/{profileTagId}")
    public ResponseEntity<Slice<UserProfileTagResponse>> findByProfileTagId(
            @PathVariable Long profileTagId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userProfileTagService.findByProfileTagId(profileTagId, pageable));
    }

    @PostMapping
    public ResponseEntity<UserProfileTagResponse> create(@Valid @RequestBody UserProfileTagRequest request) {
        UserProfileTagResponse created = userProfileTagService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/user/{userId}")
                .buildAndExpand(created.userId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/user/{userId}/profile-tag/{profileTagId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long profileTagId) {
        userProfileTagService.delete(userId, profileTagId);
        return ResponseEntity.noContent().build();
    }
}