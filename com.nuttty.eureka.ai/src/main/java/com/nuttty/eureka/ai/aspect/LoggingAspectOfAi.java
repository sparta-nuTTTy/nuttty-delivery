package com.nuttty.eureka.ai.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j(topic = "Ai")
public class LoggingAspectOfAi {

    // CompanyController, CompanyService 포인트 컷
    @Pointcut("execution(* com.nuttty.eureka.ai.presentation.controller.AiController..*(..)) || execution(* com.nuttty.eureka.ai.application.service.AiService..*(..))")
    public void aiPackagePointcut() {
    }

    // 메서드 실행 전후로 로그 기록 (메서드 실행 시간 측정)
    @Around("aiPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        long startTime = System.currentTimeMillis();
        log.info("메서드 실행 시작 - 클래스명: {}, 메서드명: {}", className, methodName);

        Object result = null;
        try {
            result = joinPoint.proceed(); // 실제 메서드 실행
        } catch (Throwable ex) {
            log.error("예외 발생 - 클래스명: {}, 메서드명: {}, 예외 메시지: {}", className, methodName, ex.getMessage());
            throw ex; // 예외를 다시 던져줍니다.
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("메서드 실행 종료 - 클래스명: {}, 메서드명: {}, 실행 시간: {}ms", className, methodName, elapsedTime);

        return result;
    }
}
