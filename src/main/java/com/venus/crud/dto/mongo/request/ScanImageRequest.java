package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ScanImageRequest(
        @Schema(description = "Identificador público que o Cloudinary devolveu no upload.",
                example = "scans/8d3f2c1a-4b5e-4f6a-9c7d-1e2f3a4b5c6d/front")
        @NotBlank @Size(max = 255) String publicId,
        @Schema(description = "Largura da imagem em pixels.", example = "1080")
        @Positive Integer width,
        @Schema(description = "Altura da imagem em pixels.", example = "1920")
        @Positive Integer height,
        @Schema(description = "Formato da imagem.", example = "jpg")
        @Size(max = 10) String format,
        @Schema(description = "Tamanho do arquivo em bytes.", example = "245760")
        @Positive Long bytes
) {
}
