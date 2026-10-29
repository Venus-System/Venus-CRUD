package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ScanUploadSignatureResponse(
        @Schema(description = "Identificador público que a foto tem que receber no Cloudinary.",
                example = "scans/8d3f2c1a-4b5e-4f6a-9c7d-1e2f3a4b5c6d/front")
        String publicId,
        @Schema(description = "Assinatura dos parâmetros do upload; vale por 1 hora.",
                example = "a1b2c3d4e5f60718293a4b5c6d7e8f9012345678")
        String signature
) {
}
