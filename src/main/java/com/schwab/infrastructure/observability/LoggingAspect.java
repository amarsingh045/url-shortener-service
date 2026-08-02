package com.schwab.infrastructure.observability;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private static final String SERVICE_POINTCUT = "execution(* com.schwab.service..*(..))";
    private static final String CONTROLLER_POINTCUT = "execution(* com.schwab.controller..*(..))";
    private static final String OBSERVED_POINTCUT = SERVICE_POINTCUT + " || " + CONTROLLER_POINTCUT;

    @Before(OBSERVED_POINTCUT)
    public void logBefore(JoinPoint joinPoint) {
        log.info("Executing {}.{} {} with arguments {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                requestContext(),
                joinPoint.getArgs());
    }

    @AfterReturning(pointcut = OBSERVED_POINTCUT, returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Completed {}.{} {} with result {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                requestContext(),
                result);
    }

    @AfterThrowing(pointcut = OBSERVED_POINTCUT, throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("Exception in {}.{} {}: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                requestContext(),
                error.getMessage(),
                error);
    }

    private String requestContext() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            return "[no-http-request]";
        }
        HttpServletRequest request = servletAttributes.getRequest();
        return "[" + request.getMethod() + " " + request.getRequestURI() + "]";
    }
}
