package com.usrun.core.aspect;

import com.usrun.core.utility.ObjectUtils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author phuctt4
 */

@Aspect
@Component
public class RequestResponseLogger {

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void controller() {
  }

  @Before("controller()")
  public void logBeforeAction(JoinPoint joinPoint) {
    Class clazz = joinPoint.getTarget().getClass();
    Logger logger = LoggerFactory.getLogger(clazz);

    String url = getRequestUrl(joinPoint, clazz);
    String payload = getPayload(joinPoint);
    logger.info("[Request]: {}, [Payload]: {}", url, payload);
  }

  @AfterReturning(pointcut = "controller()", returning = "result")
  public void logAfterAction(JoinPoint joinPoint, Object result) {
//              String returnValue = this.getValue(result);
//              log.debug("Method Return value : " + returnValue);
    Class clazz = joinPoint.getTarget().getClass();
    Logger logger = LoggerFactory.getLogger(clazz);
    String url = getRequestUrl(joinPoint, clazz);

    logger.info("[Response]: {}, [Payload]: {}", url, getResponse(result));
  }

  private String getRequestUrl(JoinPoint joinPoint, Class clazz) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method method = methodSignature.getMethod();
    PostMapping methodPostMapping = method.getAnnotation(PostMapping.class);
    RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
    return getBaseUrl(requestMapping, methodPostMapping);
  }

  private String getPayload(JoinPoint joinPoint) {
    CodeSignature signature = (CodeSignature) joinPoint.getSignature();
    Map<String, Object> map = new HashMap<>();
    for (int i = 0; i < joinPoint.getArgs().length; i++) {
      map.put(signature.getParameterNames()[i], joinPoint.getArgs()[i]);
    }
    return ObjectUtils.toJsonString(map);
  }

  private String getResponse(Object result) {
    ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
    return ObjectUtils.toJsonString(responseEntity.getBody());
  }

  private String getBaseUrl(RequestMapping requestMapping, PostMapping postMapping) {
    String baseUrl = getUrl(requestMapping.value());
    String endpoint = getUrl(postMapping.value());
    return baseUrl + endpoint;
  }

  private String getUrl(String[] urls) {
    if (urls.length == 0) {
      return "";
    } else {
      return urls[0];
    }
  }

}
