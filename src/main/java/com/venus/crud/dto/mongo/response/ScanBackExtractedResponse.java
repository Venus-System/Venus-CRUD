package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ScanBackExtractedResponse(
        @Schema(description = "Trecho inteiro da composição, como o OCR leu.",
                example = "ALCOHOL DENAT, AQUA, PARFUM, COUMARLN, GERANIOL")
        String ingredientsText,
        @Schema(description = "Fabricante.", example = "Venus Cosméticos Ltda.")
        String manufacturer,
        @Schema(description = "Endereço do fabricante.", example = "Rua das Flores, 100, São Paulo - SP")
        String manufacturerAddress,
        @Schema(description = "Contato do atendimento ao consumidor.", example = "0800 000 0000")
        String contact,
        @Schema(description = "País de fabricação.", example = "Brasil")
        String country,
        @Schema(description = "Lote.", example = "L2409A")
        String batch,
        @Schema(description = "Número de registro na Anvisa.", example = "25351.000000/2024-00")
        String registrationNumber,
        @Schema(description = "Conteúdo líquido.", example = "200 ml")
        String netContent,
        @Schema(description = "Modo de uso.", example = "Aplique sobre a pele limpa.")
        String usage,
        @Schema(description = "Precauções.", example = "Evite contato com os olhos.")
        String precautions,
        @Schema(description = "Advertências.", example = "Mantenha fora do alcance de crianças.")
        String warnings,
        @Schema(description = "Código de barras.", example = "7891033000000")
        String barcode,
        @Schema(description = "Alegações impressas no verso.")
        List<String> claims,
        @Schema(description = "Texto do verso que não se encaixou em nenhum campo.", example = "Produto não testado em animais.")
        String otherText
) {
}
