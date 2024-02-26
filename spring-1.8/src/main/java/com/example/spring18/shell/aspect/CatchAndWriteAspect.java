package com.example.spring18.shell.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CatchAndWriteAspect {

    @Around("@annotation(CatchAndWrite)")
    public Object catchAndWriteExceptionMessage(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
