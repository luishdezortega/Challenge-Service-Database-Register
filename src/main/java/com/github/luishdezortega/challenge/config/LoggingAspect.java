package com.github.luishdezortega.challenge.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.luishdezortega.challenge.Util.Constants;
import com.github.luishdezortega.challenge.model.CallLogEntity;
import com.github.luishdezortega.challenge.repository.CallLogRepository;
import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingAspect implements ApplicationContextAware {

    private final CallLogRepository callLogRepository;
    private final ObjectMapper objectMapper;

    private ApplicationContext applicationContext;

    @Pointcut("execution(* com.github.luishdezortega.challenge.controller..*(..)) && @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void controllerMethods() {
    }

    @AfterReturning(value = "controllerMethods()", returning = "result")
    public void logAfterSuccess(JoinPoint joinPoint, Object result) {
        getSelf().saveCallLog(joinPoint, result, null);
    }

    @Async
    public void saveCallLog(JoinPoint joinPoint, Object response, Throwable exception) {
        try {
            var signature = (MethodSignature) joinPoint.getSignature();
            var methodName = signature.getMethod().getName();
            var endpoint = signature.getDeclaringTypeName() + "." + methodName;
            var paramsJson = serializeParamsToJson(joinPoint.getArgs());
            var result = serializeResponse(response, exception);
            var timeOfRequest = ZonedDateTime.now(ZoneId.of(Constants.TIMEZONE_COLOMBIA)).toLocalDateTime();

            var logEntry = CallLogEntity.builder()
                    .timestamp(timeOfRequest)
                    .endpoint(endpoint)
                    .parameters(paramsJson)
                    .response(result)
                    .build();

            callLogRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Error al guardar el log de la llamada", e);
        }
    }

    private String serializeParamsToJson(Object[] args) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(args);
        } catch (JsonProcessingException e) {
            log.error("Error al serializar parámetros", e);
            return "[]";
        }
    }

    private String serializeResponse(Object response, Throwable exception) {
        try {
            if (response != null) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
            } else if (exception != null) {
                return exception.getMessage();
            } else {
                return "N/A";
            }
        } catch (JsonProcessingException e) {
            log.error("Error al serializar respuesta", e);
            return "Error al serializar respuesta";
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private LoggingAspect getSelf() {
        return applicationContext.getBean(LoggingAspect.class);
    }
}