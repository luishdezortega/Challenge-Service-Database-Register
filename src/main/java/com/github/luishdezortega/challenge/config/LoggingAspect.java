package com.github.luishdezortega.challenge.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.luishdezortega.challenge.model.CallLogEntity;
import com.github.luishdezortega.challenge.repository.CallLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final CallLogRepository callLogRepository;
    private final ObjectMapper objectMapper;

    @Lazy
    private final ApplicationContext applicationContext;

    @Pointcut("execution(* com.github.luishdezortega.challenge.controller.CalculatorController..*(..)) && @annotation(org.springframework.web.bind.annotation.PostMapping))")
    public void controllerMethods() {
    }

    @AfterReturning(value = "controllerMethods()", returning = "result")
    public void logAfterSuccess(JoinPoint joinPoint, Object result) {
        getSelf().saveCallLog(joinPoint, result, null);
    }

    @AfterThrowing(value = "controllerMethods()", throwing = "exception")
    public void logAfterException(JoinPoint joinPoint, Throwable exception) {
        getSelf().saveCallLog(joinPoint, null, exception);
    }

    @Async
    public void saveCallLog(JoinPoint joinPoint, Object response, Throwable exception) {
        var signature = (MethodSignature) joinPoint.getSignature();
        var methodName = signature.getMethod().getName();
        var endpoint = signature.getDeclaringTypeName() + "." + methodName;
        var paramsJson = serializeParamsToJson(joinPoint.getArgs());
        var result = (response != null) ? response.toString() : (exception != null ? exception.getMessage() : "N/A");

        var logEntry = new CallLogEntity(null, LocalDateTime.now(), endpoint, paramsJson, result);
        callLogRepository.save(logEntry);
    }

    private LoggingAspect getSelf() {
        return applicationContext.getBean(LoggingAspect.class);
    }

    private String serializeParamsToJson(Object[] args) {
        try {
            return objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            log.error("❌ Error serializing parameters", e);
            return "Serialization error";
        }
    }
}
