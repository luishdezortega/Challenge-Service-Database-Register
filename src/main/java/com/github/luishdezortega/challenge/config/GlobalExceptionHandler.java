package com.github.luishdezortega.challenge.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.luishdezortega.challenge.exception.DatabaseConnectionException;
import com.github.luishdezortega.challenge.exception.PercentageUnavailableException;
import com.github.luishdezortega.challenge.util.Constants;
import com.github.luishdezortega.challenge.dto.CallLogDTO;
import com.github.luishdezortega.challenge.service.ICallLogService;
import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ApplicationContextAware {

    private final ICallLogService callLogService;
    private final ObjectMapper objectMapper;
    private ApplicationContext applicationContext;


    @ExceptionHandler(DatabaseConnectionException.class)
    public ResponseEntity<Map<String, String>> handleDatabaseConnectionException(DatabaseConnectionException ex, WebRequest request) {
        log.error("DatabaseConnectionException handled: {}", ex.getMessage());
        getSelf().saveErrorLog(request, ex);
        Map<String, String> response = new HashMap<>();
        response.put("error", "Database connection error occurred.");
        response.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(PercentageUnavailableException.class)
    public ResponseEntity<Map<String, String>> handlePercentageUnavailableException(PercentageUnavailableException ex, WebRequest request) {
        log.error("PercentageUnavailableException handled: {}", ex.getMessage());
        getSelf().saveErrorLog(request, ex);
        Map<String, String> response = new HashMap<>();
        response.put("error", "Percentage service is currently unavailable.");
        response.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex, WebRequest request) {
        log.error("Error unexpected: {}", ex.getMessage());
        getSelf().saveErrorLog(request, ex);
        Map<String, String> response = new HashMap<>();
        response.put("error", "An unexpected error occurred");
        response.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    public void saveErrorLog(WebRequest request, Throwable exception) {
        var endpoint = request.getDescription(false).replace("uri=", "");
        var paramsJson = serializeParamsToJson(request.getParameterMap());
        var errorMessage = exception.getMessage();
        var timeOfRequest = ZonedDateTime.now(ZoneId.of(Constants.TIMEZONE_COLOMBIA)).toLocalDateTime();
        var logEntry = new CallLogDTO(timeOfRequest, endpoint, paramsJson, errorMessage);
        callLogService.saveCallLogs(logEntry);
    }

    private String serializeParamsToJson(Map<String, String[]> params) {
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            log.error("Error al serializar parámetros: {}", e.getMessage());
            return "{}";
        }
    }

    private GlobalExceptionHandler getSelf() {
        return applicationContext.getBean(GlobalExceptionHandler.class);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
