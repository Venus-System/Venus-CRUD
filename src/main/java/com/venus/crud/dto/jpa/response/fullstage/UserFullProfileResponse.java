package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.media.MediaAssetResponse;
import com.venus.crud.dto.jpa.response.product.ProductResponse;
import com.venus.crud.dto.jpa.response.shared.ProfileTagResponse;
import com.venus.crud.dto.jpa.response.user.UserPreferenceResponse;
import com.venus.crud.dto.jpa.response.user.UserProfileResponse;
import com.venus.crud.dto.jpa.response.user.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record UserFullProfileResponse(
        @Schema(description = "Usuário dono do registro.")
        UserResponse user,
        @Schema(description = "Imagem de perfil do usuário.")
        MediaAssetResponse avatar,
        @Schema(description = "Perfil físico do usuário.")
        UserProfileResponse profile,
        @Schema(description = "Tags de perfil marcadas pelo usuário.")
        List<ProfileTagResponse> tags,
        @Schema(description = "Preferências do usuário.")
        UserPreferenceResponse preferences,
        @Schema(description = "Alergias registradas para o usuário.")
        List<UserAllergyDetailResponse> allergies,
        @Schema(description = "Produtos favoritados pelo usuário.")
        List<ProductResponse> favorites,
        @Schema(description = "Listas criadas pelo usuário, com os itens dentro.")
        List<UserListWithItemsResponse> lists
) {
}
