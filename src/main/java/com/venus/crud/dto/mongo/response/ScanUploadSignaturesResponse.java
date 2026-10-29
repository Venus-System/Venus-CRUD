package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ScanUploadSignaturesResponse(
        @Schema(description = "Nome da conta no Cloudinary.", example = "venus")
        String cloudName,
        @Schema(description = "Chave pública da API do Cloudinary, enviada junto com o upload.", example = "123456789012345")
        String apiKey,
        @Schema(description = "Momento da assinatura, em segundos desde 1970 (UTC).", example = "1790265600")
        long timestamp,
        @Schema(description = "Preset de upload assinado; vazio quando a conta não usa preset.", example = "venus_products")
        String uploadPreset,
        @Schema(description = "Tipo de entrega da imagem no Cloudinary.", example = "upload")
        String type,
        @Schema(description = "Indica se o upload substitui uma foto já enviada no mesmo lugar.", example = "true")
        boolean overwrite,
        @Schema(description = "Assinatura da foto da frente.")
        ScanUploadSignatureResponse front,
        @Schema(description = "Assinatura da foto do verso.")
        ScanUploadSignatureResponse back
) {
}
