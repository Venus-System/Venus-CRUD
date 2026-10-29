package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.user.UserListResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record UserListWithItemsResponse(
        @Schema(description = "Lista a que o item pertence.")
        UserListResponse list,
        @Schema(description = "Itens que estão dentro da lista.")
        List<UserListItemDetailResponse> items
) {
}
