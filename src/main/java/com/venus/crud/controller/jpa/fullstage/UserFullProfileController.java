package com.venus.crud.controller.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.UserFullProfileResponse;
import com.venus.crud.service.jpa.fullstage.UserFullProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Cadastro, busca, ciclo de vida, avatar e perfil completo do usuário.")
public class UserFullProfileController {

    private final UserFullProfileService userFullProfileService;

    public UserFullProfileController(UserFullProfileService userFullProfileService) {
        this.userFullProfileService = userFullProfileService;
    }

    @Operation(
            operationId = "userFullProfileFindByUserId",
            summary = "Busca o perfil completo do usuário: dados, preferências, alergias e avatar",
            description = "Agregado montado para a tela de perfil: dados do usuário, avatar, perfil físico, tags marcadas, "
                    + "preferências, alergias, favoritos e listas com os itens dentro. Evita as oito chamadas "
                    + "separadas.")
    @GetMapping("/{userId}/full-profile")
    public ResponseEntity<UserFullProfileResponse> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userFullProfileService.findByUserId(userId));
    }
}