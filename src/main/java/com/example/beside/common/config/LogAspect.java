package com.example.beside.common.config;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.beside.util.JwtProvider;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;


@Aspect
@Component
public class LogAspect {
    @Autowired
    private JwtProvider jwtprovider;

    @Autowired
    private EntityManager em;
    
    // Loggable 어노테이션이 클래스에 적용되어 있는 경우에만
    @Pointcut("@within(Loggable)")
    public void loggableClass() {
    }

    // API
    @Before("loggableClass() && execution(* *(..))")
    public void logBeforeMethod(JoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        Object[] args = joinPoint.getArgs();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        logger.info("==========================");
        logger.info("API 요청: " + joinPoint.getSignature().toShortString());
        logger.info("메서드 인자: {}", Arrays.toString(args));
        logger_info_user_id(logger, request);
    }

    // Exception 
    @Transactional
    @AfterThrowing(pointcut = "loggableClass() && execution(* *(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        Object[] args = joinPoint.getArgs();
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        logger.info("==========================");
        logger.info("API 요청: " + joinPoint.getSignature().toShortString());
        logger.info("메서드 인자: {}", Arrays.toString(args));
        logger.error("예외 타입: " + ex.getClass().getSimpleName());
        logger.error("예외 발생: " + ex.getMessage());
        logger_info_user_id(logger, request);
    }

    private void logger_info_user_id(Logger logger, HttpServletRequest request) {
        if (request.getHeader("Authorization") != null){
            String jwt = request.getHeader("Authorization").split(" ")[1];
            Object user_id = jwtprovider.validJwtToken(jwt).get("user_id");
            logger.info("jwt :  {}", jwt);
            logger.info("유저 id:  {}", user_id);
        }
    }
}
