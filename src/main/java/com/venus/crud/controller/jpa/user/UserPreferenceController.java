package com.venus.crud.controller.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserPreferencePatchRequest;
import com.venus.crud.dto.jpa.request.user.UserPreferenceRequest;
import com.venus.crud.dto.jpa.response.user.UserPreferenceResponse;
import com.venus.crud.service.jpa.user.UserPreferenceService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/user-preferences")
@Tag(name = "Preferências do Usuário", description = "O que o usuário marcou na tela de onboarding.")
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    public UserPreferenceController(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

    @Operation(operationId = "userPreferenceFindAll", summary = "Lista as preferências do usuário")
    @GetMapping
    public ResponseEntity<List<UserPreferenceResponse>> findAll() {
        return ResponseEntity.ok(userPreferenceService.findAll());
    }

    @Operation(operationId = "userPreferenceSearch", summary = "Busca as preferências do usuário com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<UserPreferenceResponse>> search(
            @RequestParam(required = false) Boolean preferCrueltyFree,
            @RequestParam(required = false) Boolean preferVegan,
            @RequestParam(required = false) Boolean preferSustainable,
            @RequestParam(required = false) Boolean preferFragranceFree,
            @RequestParam(required = false) Boolean preferParabenFree,
            @RequestParam(required = false) Boolean preferSulfateFree,
            @RequestParam(required = false) Boolean preferSiliconeFree,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userPreferenceService.search(preferCrueltyFree, preferVegan, preferSustainable,
                preferFragranceFree, preferParabenFree, preferSulfateFree, preferSiliconeFree, pageable));
    }

    @Operation(operationId = "userPreferenceFindByUserId", summary = "Busca as preferências de um usuário")
    @GetMapping("/{userId}")
    public ResponseEntity<UserPreferenceResponse> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userPreferenceService.findByUserId(userId));
    }

    @Operation(operationId = "userPreferenceCreate", summary = "Cadastra uma preferência do usuário")
    @PostMapping
    public ResponseEntity<UserPreferenceResponse> create(@Valid @RequestBody UserPreferenceRequest request) {
        UserPreferenceResponse created = userPreferenceService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(created.userId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "userPreferenceUpdate", summary = "Substitui os dados da preferência do usuário")
    @PutMapping("/{userId}")
    public ResponseEntity<UserPreferenceResponse> update(@PathVariable Long userId, @Valid @RequestBody UserPreferenceRequest request) {
        return ResponseEntity.ok(userPreferenceService.update(userId, request));
    }

    @Operation(operationId = "userPreferencePatch", summary = "Atualiza parcialmente a preferência do usuário")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserPreferenceResponse> patch(@PathVariable Long userId, @Valid @RequestBody UserPreferencePatchRequest request) {
        return ResponseEntity.ok(userPreferenceService.patch(userId, request));
    }

    @Operation(operationId = "userPreferenceDelete", summary = "Remove a preferência do usuário")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userPreferenceService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}