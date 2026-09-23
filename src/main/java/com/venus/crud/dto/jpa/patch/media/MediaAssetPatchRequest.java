package com.venus.crud.dto.jpa.patch.media;
import io.swagger.v3.oas.annotations.media.Schema;


public record MediaAssetPatchRequest(
        @Schema(description = "Texto alternativo da imagem, usado por leitores de tela.",
                example = "Frasco do shampoo visto de frente")
        String altText,
        @Schema(description = "Ordem de exibição da foto.", example = "1")
        Integer sortOrder
) {
}
