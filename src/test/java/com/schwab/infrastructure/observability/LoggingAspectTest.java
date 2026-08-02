package com.schwab.infrastructure.observability;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class LoggingAspectTest {

    private final LoggingAspect aspect = new LoggingAspect();

    @Test
    void shouldLogWithoutHttpRequestContext() {
        RequestContextHolder.resetRequestAttributes();
        JoinPoint joinPoint = mockJoinPoint();

        aspect.logBefore(joinPoint);
        aspect.logAfterReturning(joinPoint, "ok");
        aspect.logAfterThrowing(joinPoint, new RuntimeException("boom"));
    }

    @Test
    void shouldLogWithHttpRequestContext() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/shorten");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        JoinPoint joinPoint = mockJoinPoint();

        aspect.logBefore(joinPoint);
        aspect.logAfterReturning(joinPoint, "ok");
        aspect.logAfterThrowing(joinPoint, new RuntimeException("boom"));

        RequestContextHolder.resetRequestAttributes();
    }

    private JoinPoint mockJoinPoint() {
        JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        Signature signature = Mockito.mock(Signature.class);
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
        Mockito.when(signature.getDeclaringTypeName()).thenReturn("com.schwab.Sample");
        Mockito.when(signature.getName()).thenReturn("sampleMethod");
        Mockito.when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1"});
        return joinPoint;
    }
}
