package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ScanApproveRequest(
        @Schema(description = "Produto do catálogo: um existente (productId) ou os dados de um novo.")
        @NotNull @Valid ScanProductDecisionRequest product,
        @Schema(description = "Decisões por ingrediente. Obrigatórias nos ingredientes AMBIGUOUS e NEW; nos KNOWN e ALIAS "
                + "servem para trocar ou descartar.")
        @Valid List<ScanIngredientDecisionRequest> ingredients,
        @Schema(description = "Observação do administrador sobre a aprovação.", example = "Conferido com o site da marca")
        @Size(max = 1000) String reason
) {
}
