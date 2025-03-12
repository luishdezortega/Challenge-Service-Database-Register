package com.github.luishdezortega.challenge.controller;


import com.github.luishdezortega.challenge.dto.CalculatorRequestDTO;
import com.github.luishdezortega.challenge.dto.CalculatorRespondDTO;
import com.github.luishdezortega.challenge.service.ICalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Calculator API", description = "Endpoints para cálculos con porcentaje")
public class CalculatorController {

    private final ICalculatorService calculatorService;

    @PostMapping("/percentage")
    @Operation(
            summary = "Calcula la suma con un porcentaje adicional",
            description = "Recibe dos números, los suma y aplica un porcentaje dinámico",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cálculo exitoso",
                            content = @Content(schema = @Schema(implementation = CalculatorRespondDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<CalculatorRespondDTO> calculator(@RequestBody CalculatorRequestDTO request) {
        double result = calculatorService.sumWithPercentage(request.numberOne(), request.numberTwo());
        return ResponseEntity.ok(new CalculatorRespondDTO(result));
    }

}
