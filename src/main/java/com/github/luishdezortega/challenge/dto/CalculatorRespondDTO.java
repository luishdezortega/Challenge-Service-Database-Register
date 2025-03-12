package com.github.luishdezortega.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con el resultado del cálculo")
public record CalculatorRespondDTO(
        @Schema(description = "Resultado final", example = "165.0") double result
) {
}
