package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ScanBackExtractedRequest(
        @Schema(description = "Trecho inteiro da composição, como o OCR leu.",
                example = "ALCOHOL DENAT, AQUA, PARFUM, COUMARLN, GERANIOL")
        @Size(max = 20000) String ingredientsText,
        @Schema(description = "Fabricante.", example = "Venus Cosméticos Ltda.")
        @Size(max = 2000) String manufacturer,
        @Schema(description = "Endereço do fabricante.", example = "Rua das Flores, 100, São Paulo - SP")
        @Size(max = 2000) String manufacturerAddress,
        @Schema(description = "Contato do atendimento ao consumidor.", example = "0800 000 0000")
        @Size(max = 2000) String contact,
        @Schema(description = "País de fabricação.", example = "Brasil")
        @Size(max = 2000) String country,
        @Schema(description = "Lote.", example = "L2409A")
        @Size(max = 2000) String batch,
        @Schema(description = "Número de registro na Anvisa.", example = "25351.000000/2024-00")
        @Size(max = 2000) String registrationNumber,
        @Schema(description = "Conteúdo líquido.", example = "200 ml")
        @Size(max = 2000) String netContent,
        @Schema(description = "Modo de uso.", example = "Aplique sobre a pele limpa.")
        @Size(max = 2000) String usage,
        @Schema(description = "Precauções.", example = "Evite contato com os olhos.")
        @Size(max = 2000) String precautions,
        @Schema(description = "Advertências.", example = "Mantenha fora do alcance de crianças.")
        @Size(max = 2000) String warnings,
        @Schema(description = "Código de barras.", example = "7891033000000")
        @Size(max = 2000) String barcode,
        @Schema(description = "Alegações impressas no verso.")
        @Size(max = 50) List<@Size(max = 200) String> claims,
        @Schema(description = "Texto do verso que não se encaixou em nenhum campo.", example = "Produto não testado em animais.")
        @Size(max = 2000) String otherText
) {
}
