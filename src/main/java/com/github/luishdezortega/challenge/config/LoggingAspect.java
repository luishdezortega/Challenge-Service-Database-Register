package com.github.luishdezortega.challenge.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.luishdezortega.challenge.util.Constants;
import com.github.luishdezortega.challenge.dto.CallLogDTO;
import com.github.luishdezortega.challenge.service.ICallLogService;
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
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect implements ApplicationContextAware {

    private final ICallLogService callLogService;
    private final ObjectMapper objectMapper;

    private ApplicationContext applicationContext;

    @Pointcut("execution(* com.github.luishdezortega.challenge.controller..*(..)) && @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void controllerMethods() {
    }

    @AfterReturning(value = "controllerMethods()", returning = "result")
    public void logAfterSuccess(JoinPoint joinPoint, Object result) {
        getSelf().saveCallLog(joinPoint, result, null);
    }

    public void saveCallLog(JoinPoint joinPoint, Object response, Throwable exception) {
        var signature = (MethodSignature) joinPoint.getSignature();
        var methodName = signature.getMethod().getName();
        var endpoint = signature.getDeclaringTypeName() + "." + methodName;
        var paramsJson = serializeParamsToJson(joinPoint.getArgs());
        var result = serializeResponse(response, exception);
        var timeOfRequest = ZonedDateTime.now(ZoneId.of(Constants.TIMEZONE_COLOMBIA)).toLocalDateTime();
        var logEntry = new CallLogDTO(timeOfRequest, endpoint, paramsJson, result);
        callLogService.saveCallLogs(logEntry);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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

    private LoggingAspect getSelf() {
        return applicationContext.getBean(LoggingAspect.class);
    }
}