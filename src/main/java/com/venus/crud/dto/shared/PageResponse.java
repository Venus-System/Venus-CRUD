package com.venus.crud.dto.shared;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
        @Schema(description = "Os registros desta página.")
        List<T> content,
        @Schema(description = "Número da página, começando em 0.", example = "0")
        int page,
        @Schema(description = "Quantidade de registros por página.", example = "20")
        int size,
        @Schema(description = "Total de registros em todas as páginas.", example = "137")
        long totalElements,
        @Schema(description = "Quantidade de páginas.", example = "7")
        int totalPages,
        @Schema(description = "Indica se esta é a última página.", example = "true")
        boolean last
) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
