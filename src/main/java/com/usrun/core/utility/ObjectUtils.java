package com.usrun.core.utility;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author phuctt4
 */

@Slf4j
public class ObjectUtils {

  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
        .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
        .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        .setSerializationInclusion(Include.NON_NULL);
  }

  public static String toJsonString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("", e);
      return null;
    }
  }

  public static <T> T fromJsonString(String sJson, Class<T> t) {
    try {
      return objectMapper.readValue(sJson, t);
    } catch (JsonProcessingException e) {
      log.error("", e);
      return null;
    }
  }

  public static <T> T fromJsonString(String sJson, TypeReference<T> tTypeReference) {
    try {
      return objectMapper.readValue(sJson, tTypeReference);
    } catch (JsonProcessingException e) {
      log.error("", e);
      return null;
    }
  }
}
