package com.github.luishdezortega.challenge.controller;

import com.github.luishdezortega.challenge.dto.RequestHistoryResponseDTO;
import com.github.luishdezortega.challenge.exception.BadRequestException;
import com.github.luishdezortega.challenge.exception.InternalServiceException;
import com.github.luishdezortega.challenge.service.ICallLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Call Log API", description = "Registro de llamadas con historial paginado")
public class CallLogController {

    private final ICallLogService callLogService;

    @GetMapping("logs")
    @Operation(
            summary = "Obtener historial de llamadas",
            description = "Recupera un historial paginado de llamadas registradas en la base de datos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Historial recuperado exitosamente", content = @Content(schema = @Schema(implementation = RequestHistoryResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = BadRequestException.class))),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(implementation = InternalServiceException.class)))
            }
    )
    public ResponseEntity<RequestHistoryResponseDTO> getCallLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp,asc") String sort) {
        return ResponseEntity.ok(callLogService.getCallLogs(page, size, sort));
    }

}
