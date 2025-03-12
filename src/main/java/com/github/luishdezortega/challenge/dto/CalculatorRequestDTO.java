package com.github.luishdezortega.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Petición para calcular suma con porcentaje")
public record CalculatorRequestDTO(
        @Schema(description = "Primer número", example = "100") double numberOne,
        @Schema(description = "Segundo número", example = "50") double numberTwo
) {
}
