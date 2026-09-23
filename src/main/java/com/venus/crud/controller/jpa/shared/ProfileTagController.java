package com.venus.crud.controller.jpa.shared;

import com.venus.crud.dto.jpa.patch.shared.ProfileTagPatchRequest;
import com.venus.crud.dto.jpa.request.shared.ProfileTagRequest;
import com.venus.crud.dto.jpa.response.shared.PreferenceCatalogResponse;
import com.venus.crud.dto.jpa.response.shared.ProfileTagResponse;
import com.venus.crud.entity.enums.ProfileTagCategory;
import com.venus.crud.service.jpa.shared.ProfileTagService;
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
@RequestMapping("/api/profile-tags")
@Tag(name = "Tags de Perfil", description = "Catálogo de tags de perfil e as tags marcadas por cada usuário.")
public class ProfileTagController {

    private final ProfileTagService profileTagService;

    public ProfileTagController(ProfileTagService profileTagService) {
        this.profileTagService = profileTagService;
    }

    @Operation(operationId = "profileTagFindAll", summary = "Lista as tags de perfil")
    @GetMapping
    public ResponseEntity<List<ProfileTagResponse>> findAll() {
        return ResponseEntity.ok(profileTagService.findAll());
    }

    @Operation(operationId = "profileTagSearch", summary = "Busca as tags de perfil com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<ProfileTagResponse>> search(
            @RequestParam(required = false) ProfileTagCategory category,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(profileTagService.search(category, pageable));
    }

    @Operation(
            operationId = "profileTagFindPreferenceCatalog",
            summary = "Lista o catálogo de preferências que monta a tela de onboarding",
            description = "Lista não paginada, ordenada por nome, com as tags das categorias VALUES e SUSTAINABILITY. É o "
                    + "que desenha a tela de onboarding.\n\nO campo `userPreferenceField` diz **onde gravar** a "
                    + "escolha do usuário: quando vem preenchido (ex: `preferVegan`), a marcação vai no booleano "
                    + "correspondente de `user_preferences`; quando vem nulo, a marcação vira uma linha em "
                    + "`user_profile_tags`. Não existe FK entre as duas tabelas — o elo é o usuário.")
    @GetMapping("/preferences")
    public ResponseEntity<List<PreferenceCatalogResponse>> findPreferenceCatalog() {
        return ResponseEntity.ok(profileTagService.findPreferenceCatalog());
    }

    @Operation(operationId = "profileTagFindById", summary = "Busca a tag de perfil por id")
    @GetMapping("/{id}")
    public ResponseEntity<ProfileTagResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(profileTagService.findById(id));
    }

    @Operation(operationId = "profileTagCreate", summary = "Cadastra uma tag de perfil")
    @PostMapping
    public ResponseEntity<ProfileTagResponse> create(@Valid @RequestBody ProfileTagRequest request) {
        ProfileTagResponse created = profileTagService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "profileTagUpdate", summary = "Substitui os dados da tag de perfil")
    @PutMapping("/{id}")
    public ResponseEntity<ProfileTagResponse> update(@PathVariable Long id, @Valid @RequestBody ProfileTagRequest request) {
        return ResponseEntity.ok(profileTagService.update(id, request));
    }

    @Operation(operationId = "profileTagPatch", summary = "Atualiza parcialmente a tag de perfil")
    @PatchMapping("/{id}")
    public ResponseEntity<ProfileTagResponse> patch(@PathVariable Long id, @Valid @RequestBody ProfileTagPatchRequest request) {
        return ResponseEntity.ok(profileTagService.patch(id, request));
    }

    @Operation(operationId = "profileTagDelete", summary = "Remove a tag de perfil")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        profileTagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
