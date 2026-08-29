package com.spring.review.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spring.review.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogService auditLogService;

    private final ObjectMapper objectMapper =
            new ObjectMapper()
                    .registerModule(new JavaTimeModule());

    @Around("@annotation(com.spring.review.config.Auditable)")
    public Object audit(ProceedingJoinPoint joinPoint)
            throws Throwable {

        MethodSignature signature =
                (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        Auditable auditable =
                method.getAnnotation(Auditable.class);

        String entityType = auditable.entityType();
        String action = auditable.action();
        String performedBy = getCurrentUser();

        Object[] args = joinPoint.getArgs();

        Object result = joinPoint.proceed();

        try {
            Long entityId = extractEntityId(result, joinPoint);

            String newValues = null;
            if (result != null) {
                newValues = objectMapper
                        .writeValueAsString(result);
            }

            if (entityId != null) {
                auditLogService.record(
                        entityType,
                        entityId,
                        action,
                        null,
                        newValues,
                        performedBy
                );
            }
        } catch (Exception e) {
            log.error(
                    "Failed to record audit log: {}",
                    e.getMessage()
            );
        }

        return result;
    }

    private String getCurrentUser() {
        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (auth != null && auth.getName() != null) {
            return auth.getName();
        }

        return "system";
    }

    private Long extractEntityId(Object result, ProceedingJoinPoint joinPoint) {
        if (result != null) {
            try {
                var getIdMethod = result.getClass()
                        .getMethod("getId");

                Object id = getIdMethod.invoke(result);

                if (id instanceof Long longId) {
                    return longId;
                }
            } catch (Exception ignored) {
            }
        }

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Long id) {
                return id;
            }
        }

        return null;
    }
}
