package com.venus.crud.dto.jpa.response.media;

import com.venus.crud.entity.enums.MediaPurpose;
import com.venus.crud.entity.enums.MediaStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record MediaAssetResponse(
        @Schema(description = "Identificador do arquivo de mídia.", example = "77")
        Long mediaAssetId,
        @Schema(description = "Finalidade do arquivo de mídia.")
        MediaPurpose purpose,
        @Schema(description = "Endereço público do arquivo.",
                example = "https://res.cloudinary.com/venus/image/upload/v1/users/42/avatar.jpg")
        String url,
        @Schema(description = "Identificador do arquivo no Cloudinary.", example = "users/42/avatar/8f3c1d")
        String publicId,
        @Schema(description = "Texto alternativo da imagem, usado por leitores de tela.",
                example = "Frasco do shampoo visto de frente")
        String altText,
        @Schema(description = "Largura da imagem, em pixels.", example = "1920")
        Integer width,
        @Schema(description = "Altura da imagem, em pixels.", example = "1080")
        Integer height,
        @Schema(description = "Formato do arquivo.", example = "jpg")
        String format,
        @Schema(description = "Ordem de exibição da foto.", example = "1")
        Integer sortOrder,
        @Schema(description = "Situação atual do registro.")
        MediaStatus status,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
