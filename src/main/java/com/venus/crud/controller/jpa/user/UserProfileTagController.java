package com.venus.crud.controller.jpa.user;

import com.venus.crud.dto.jpa.request.user.UserProfileTagRequest;
import com.venus.crud.dto.jpa.response.user.UserProfileTagResponse;
import com.venus.crud.service.jpa.user.UserProfileTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tags de Perfil", description = "Catálogo de tags de perfil e as tags marcadas por cada usuário.")
public class UserProfileTagController {

    private final UserProfileTagService userProfileTagService;

    public UserProfileTagController(UserProfileTagService userProfileTagService) {
        this.userProfileTagService = userProfileTagService;
    }

    @Operation(operationId = "userProfileTagFindAll", summary = "Lista as tags marcadas pelo usuário")
    @GetMapping
    public ResponseEntity<List<UserProfileTagResponse>> findAll() {
        return ResponseEntity.ok(userProfileTagService.findAll());
    }

    @Operation(operationId = "userProfileTagFindByUserId", summary = "Lista as tags marcadas por um usuário")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Slice<UserProfileTagResponse>> findByUserId(
            @PathVariable Long userId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userProfileTagService.findByUserId(userId, pageable));
    }

    @Operation(operationId = "userProfileTagFindByProfileTagId", summary = "Lista os usuários que marcaram uma tag")
    @GetMapping("/profile-tag/{profileTagId}")
    public ResponseEntity<Slice<UserProfileTagResponse>> findByProfileTagId(
            @PathVariable Long profileTagId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userProfileTagService.findByProfileTagId(profileTagId, pageable));
    }

    @Operation(operationId = "userProfileTagCreate", summary = "Cadastra uma tag marcada pelo usuário")
    @PostMapping
    public ResponseEntity<UserProfileTagResponse> create(@Valid @RequestBody UserProfileTagRequest request) {
        UserProfileTagResponse created = userProfileTagService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/user/{userId}")
                .buildAndExpand(created.userId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "userProfileTagDelete", summary = "Remove a tag marcada pelo usuário")
    @DeleteMapping("/user/{userId}/profile-tag/{profileTagId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long profileTagId) {
        userProfileTagService.delete(userId, profileTagId);
        return ResponseEntity.noContent().build();
    }
}