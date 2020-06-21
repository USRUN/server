package com.usrun.core.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author phuctt4
 */

@Slf4j
@Component
public class ObjectUtils {

  @Autowired
  private ObjectMapper objectMapper;

  public String toJsonString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("", e);
      return null;
    }
  }

  public <T> T fromJsonString(String sJson, Class<T> t) {
    try {
      return objectMapper.readValue(sJson, t);
    } catch (JsonProcessingException e) {
      log.error("", e);
      return null;
    }
  }

  public <T> T fromJsonString(String sJson, TypeReference<T> tTypeReference) {
    try {
      return objectMapper.readValue(sJson, tTypeReference);
    } catch (JsonProcessingException e) {
      log.error("", e);
      return null;
    }
  }
}
