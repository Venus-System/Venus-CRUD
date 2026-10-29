package com.venus.crud.dto.mongo.response;

import com.venus.crud.entity.enums.IngredientMatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record IngredientMatchResponse(
        @Schema(description = "Resultado da busca: KNOWN achou pelo INCI, ALIAS achou pelo apelido, "
                + "AMBIGUOUS achou mais de um e NEW não achou nenhum.")
        IngredientMatchStatus status,
        @Schema(description = "Ingrediente do catálogo encontrado; vazio quando não achou ou ficou ambíguo.", example = "17")
        Long ingredientId,
        @Schema(description = "Nome INCI do ingrediente encontrado.", example = "AQUA")
        String matchedName,
        @Schema(description = "Ingredientes possíveis quando o resultado é AMBIGUOUS.")
        List<IngredientCandidateResponse> candidates
) {
}
