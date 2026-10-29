package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ScanImageResponse(
        @Schema(description = "Identificador público da foto no Cloudinary.",
                example = "scans/8d3f2c1a-4b5e-4f6a-9c7d-1e2f3a4b5c6d/front")
        String publicId,
        @Schema(description = "Endereço da foto, montado pela API a partir do identificador público.",
                example = "https://res.cloudinary.com/venus/image/upload/v1/scans/8d3f2c1a-4b5e-4f6a-9c7d-1e2f3a4b5c6d/front")
        String secureUrl,
        @Schema(description = "Largura da imagem em pixels.", example = "1080")
        Integer width,
        @Schema(description = "Altura da imagem em pixels.", example = "1920")
        Integer height,
        @Schema(description = "Formato da imagem.", example = "jpg")
        String format,
        @Schema(description = "Tamanho do arquivo em bytes.", example = "245760")
        Long bytes
) {
}
