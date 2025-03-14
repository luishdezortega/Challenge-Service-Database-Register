package com.github.luishdezortega.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Petición para calcular suma con porcentaje")
public record CalculatorRequestDTO(
        @NotNull(message = "NumberOne is required and must be a valid number")
        @Schema(description = "Primer número", example = "100") double numberOne,
        @NotNull(message = "NumberTwo is required and must be a valid number")
        @Schema(description = "Segundo número", example = "50") double numberTwo
) {
}
