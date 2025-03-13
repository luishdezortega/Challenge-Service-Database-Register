package com.github.luishdezortega.challenge.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.luishdezortega.challenge.Util.Constants;
import com.github.luishdezortega.challenge.model.CallLogEntity;
import com.github.luishdezortega.challenge.repository.CallLogRepository;
import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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

    private final CallLogRepository callLogRepository;
    private final ObjectMapper objectMapper;
    private ApplicationContext applicationContext;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex, WebRequest request) {
        log.error("Error manejado: {}", ex.getMessage());
        getSelf().saveErrorLog(request, ex);
        Map<String, String> response = new HashMap<>();
        response.put("error", "Ocurrió un error inesperado");
        response.put("details", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @Async
    public void saveErrorLog(WebRequest request, Throwable exception) {
        try {
            var endpoint = request.getDescription(false).replace("uri=", "");
            var paramsJson = serializeParamsToJson(request.getParameterMap());
            var errorMessage = exception.getMessage();
            var timeOfRequest = ZonedDateTime.now(ZoneId.of(Constants.TIMEZONE_COLOMBIA)).toLocalDateTime();

            var logEntry = CallLogEntity.builder()
                    .timestamp(timeOfRequest)
                    .endpoint(endpoint)
                    .parameters(paramsJson)
                    .response(errorMessage)
                    .build();

            callLogRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Error al guardar el log de error: {}", e.getMessage());
        }
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
